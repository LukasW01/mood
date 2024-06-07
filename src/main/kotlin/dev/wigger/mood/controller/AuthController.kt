package dev.wigger.mood.controller

import dev.wigger.mood.dto.*
import dev.wigger.mood.mail.Mailgun
import dev.wigger.mood.security.HashService
import dev.wigger.mood.security.TokenService
import dev.wigger.mood.templates.Templates
import dev.wigger.mood.user.UserService
import dev.wigger.mood.user.Users
import dev.wigger.mood.util.Password
import dev.wigger.mood.util.enums.Roles

import io.quarkiverse.bucket4j.runtime.RateLimited
import io.quarkiverse.bucket4j.runtime.resolver.IpResolver
import io.quarkus.security.Authenticated
import io.vertx.ext.web.RoutingContext
import jakarta.annotation.security.PermitAll
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme

import java.time.LocalDateTime
import java.util.*

/**
 * Reference for SecurityContext:
 * https://developer.okta.com/blog/2019/09/30/java-quarkus-oidc
 */
@ApplicationScoped
@Path("/") @Produces(MediaType.APPLICATION_JSON) @Consumes(MediaType.APPLICATION_JSON)
@SecurityScheme(
    scheme = "bearer",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
)
@RateLimited(bucket = "auth", identityResolver = IpResolver::class)
class AuthController {
    @Inject
    private lateinit var tokenService: TokenService

    @Inject
    private lateinit var hashService: HashService

    @Inject
    private lateinit var userService: UserService
    
    @Inject
    private lateinit var mailgun: Mailgun

    @POST @Path("/auth/login")
    @PermitAll
    @Transactional
    fun login(@Valid payload: LoginDto, context: RoutingContext): AuthResponseDto {
        val user = userService.findByMail(payload.mail)
        if (!hashService.isHashedArgon(payload.password, user.password)) {
            throw WebApplicationException("Login failed", 403)
        }

        mailgun.sendMessage(user.mail, "Login", Templates.login(user, context.request().remoteAddress().host()).render())

        return AuthResponseDto(
            token = tokenService.createToken(user, if (user.isVerified) Roles.USER else Roles.UNVERIFIED),
            user = user.toDto(),
        )
    }
    
    @POST @Path("/auth/register")
    @PermitAll
    @Transactional
    fun register(@Valid payload: RegisterDto, context: RoutingContext) {
        userService.findByMailException(payload.mail)
        if (!Password.hasSufficientStrength(payload.password)) {
            throw WebApplicationException("Password too weak", 400)
        }

        val user = Users().apply {
            mail = payload.mail
            firstName = payload.firstName
            lastName = payload.lastName
            password = hashService.hashArgon(payload.password)
            verifyToken = UUID.randomUUID()
            isVerified = false
        }

        userService.persistOne(user)
        mailgun.sendMessage(payload.mail, "Register", Templates.register(payload, context.request().remoteAddress().host(),
            "https://${context.request().authority()}/auth/verify/${user.verifyToken}").render())
    }

    @PUT @Path("/auth/update")
    @RolesAllowed("USER")
    @Transactional
    fun update(@Valid payload: UpdateDto, ctx: SecurityContext) {
        val user = userService.findByMail(ctx.userPrincipal.name)
        if (!hashService.isHashedArgon(payload.oldPassword, user.password) || !Password.hasSufficientStrength(payload.newPassword)) {
            throw WebApplicationException("Login failed", 401)
        }

        payload.mail?.let { userService.findByMailException(it) }
        
        userService.updateOne(
            user.id,
            Users().apply {
                mail = payload.mail ?: user.mail
                firstName = payload.firstName ?: user.firstName
                lastName = payload.lastName ?: user.lastName
                password = hashService.hashArgon(payload.newPassword)
            },
        )
    }
    
    @DELETE @Path("/auth/delete")
    @RolesAllowed("USER")
    @Transactional
    fun delete(@Valid payload: DeleteDto) {
        val user = userService.findByMail(payload.mail)
        if (!hashService.isHashedArgon(payload.password, user.password)) {
            throw WebApplicationException("Login failed", 403)
        }
        
        userService.deleteByMail(payload.mail)
    }

    @GET @Path("/auth/verify/{token}")
    @Produces(MediaType.TEXT_HTML) @Consumes(MediaType.TEXT_PLAIN)
    @PermitAll
    @Transactional
    fun register(token: UUID, context: RoutingContext): String {
        val user = userService.findByVerifyToken(token)
        
        if (!user.isVerified && user.dateJoined.isAfter(LocalDateTime.now().minusDays(1))) {
            userService.updateOne(user.id, user.apply { isVerified = true })

            mailgun.sendMessage(user.mail, "Account verified!", Templates.verify(user, context.request().remoteAddress().host()).render())
        }

        return Templates.verify(user, context.request().remoteAddress().host()).render()
    }
    
    @POST @Path("/auth/password/reset")
    @PermitAll
    @Transactional
    fun reset(@Valid payload: MailResetDto, context: RoutingContext) {
        val user = userService.findByMail(payload.mail)
        val token = UUID.randomUUID()
        
        userService.updateOne(user.id, user.apply { resetToken = token })

        mailgun.sendMessage(user.mail, "Password reset", Templates.reset(user, context.request().remoteAddress().host(),
            "https://${context.request().authority()}/auth/password/reset/confirm/$token").render())
    }
    
    @GET @Path("/auth/password/reset/confirm/{token}")
    @Produces(MediaType.TEXT_HTML) @Consumes(MediaType.TEXT_PLAIN)
    @PermitAll
    fun reset(token: UUID, context: RoutingContext): String {
        val user = userService.findByResetToken(token)

        return Templates.resetForm(user, context.request().remoteAddress().host()).render()
    }
    
    @PUT @Path("/auth/password/reset/change/")
    @PermitAll
    @Transactional
    fun reset(@Valid payload: ResetDto) {
        val user = userService.findByResetToken(payload.token)
        
        if (payload.password != payload.passwordRepeat) {
            throw WebApplicationException("Passwords do not match", 400)
        }
        
        userService.updateOne(user.id, user.apply {
            password = hashService.hashArgon(payload.password)
            resetToken = null
        })
    }
    
    @POST @Path("/auth/password/reset/token")
    @PermitAll
    fun token(@Valid payload: TokenUuiddto): Response = userService.findByResetToken(payload.token).let { Response.ok().build() }
    
    @GET @Path("/auth/refresh")
    @Authenticated
    fun refresh(ctx: SecurityContext): TokenDto {
        val user = userService.findByMail(ctx.userPrincipal.name)
        
        return TokenDto(tokenService.createToken(user, if (user.isVerified) Roles.USER else Roles.UNVERIFIED))
    }
}
