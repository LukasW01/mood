package dev.wigger.mood.controller

import dev.wigger.mood.dto.AuthResponseDto
import dev.wigger.mood.dto.DeleteDto
import dev.wigger.mood.dto.LoginDto
import dev.wigger.mood.dto.RegisterDto
import dev.wigger.mood.dto.RequestResetDto
import dev.wigger.mood.dto.ResetDto
import dev.wigger.mood.dto.TokenDto
import dev.wigger.mood.dto.UpdateDto
import dev.wigger.mood.dto.toDto
import dev.wigger.mood.mail.Mailer
import dev.wigger.mood.security.HashService
import dev.wigger.mood.security.TokenService
import dev.wigger.mood.templates.Templates
import dev.wigger.mood.user.UserService
import dev.wigger.mood.user.Users
import dev.wigger.mood.util.enums.Roles
import dev.wigger.mood.util.mapper.WebApplicationMapperException
import dev.wigger.mood.util.userUuid

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
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.DELETE
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.SecurityContext
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme

import java.time.LocalDateTime
import java.util.UUID

/**
 * Reference for SecurityContext:
 * https://developer.okta.com/blog/2019/09/30/java-quarkus-oidc
 */
@ApplicationScoped
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
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
    private lateinit var mailer: Mailer
    
    @POST
    @Path("/auth/login")
    @PermitAll
    fun login(@Valid payload: LoginDto): AuthResponseDto {
        userService.findByMail(payload.mail).let { user ->
            if (!hashService.isHashedCrypt(payload.password, user.password)) {
                throw WebApplicationMapperException("Login failed", 403)
            }

            mailer.send(user.mail, "Login | mood", Templates.login(user).render()).await()
                .indefinitely()

            return AuthResponseDto(
                token = tokenService.createToken(user, if (user.isVerified) Roles.USER else Roles.UNVERIFIED),
                user = user.toDto(),
            )
        }
    }

    @POST
    @Path("/auth/register")
    @PermitAll
    @Transactional
    fun register(@Valid payload: RegisterDto, context: RoutingContext) {
        userService.findByMailExists(payload.mail)

        Users().apply {
            mail = payload.mail
            firstName = payload.firstName
            lastName = payload.lastName
            password = hashService.hash(payload.password)
            verifyToken = UUID.randomUUID()
            isVerified = false
        }.let { user ->
            userService.persistOne(user)

            mailer.send(payload.mail, "Register | mood", Templates.register(user, context).render()).await()
                .indefinitely()
        }
    }

    @PUT
    @Path("/auth")
    @RolesAllowed("USER")
    @Transactional
    fun update(@Valid payload: UpdateDto, ctx: SecurityContext) {
        userService.findByIdUuid(ctx.userUuid()).let { user ->
            if (!hashService.isHashedCrypt(payload.password, user.password)) {
                throw WebApplicationMapperException("Login failed", 403)
            }

            payload.mail?.let { userService.findByMailExists(it) }

            userService.updateOne(
                user.apply {
                    mail = payload.mail ?: user.mail
                    firstName = payload.firstName ?: user.firstName
                    lastName = payload.lastName ?: user.lastName
                },
            )
        }
    }
    
    @DELETE
    @Path("/auth")
    @RolesAllowed("USER")
    @Transactional
    fun delete(@Valid payload: DeleteDto, ctx: SecurityContext) {
        userService.findByIdUuid(ctx.userUuid()).let { user ->
            if (user.mail != payload.mail) {
                throw WebApplicationMapperException("Login failed", 403)
            }

            if (!hashService.isHashedCrypt(payload.password, user.password)) {
                throw WebApplicationMapperException("Login failed", 403)
            }

            userService.deleteByUuid(user.id)
        }
    }

    @GET
    @Path("/auth/refresh")
    @Authenticated
    fun refresh(ctx: SecurityContext): TokenDto {
        userService.findByIdUuid(ctx.userUuid()).let { user ->
            return TokenDto(tokenService.createToken(user, if (user.isVerified) Roles.USER else Roles.UNVERIFIED))
        }
    }

    @GET
    @Path("/auth/verify/{token}")
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.TEXT_PLAIN)
    @PermitAll
    @Transactional
    fun verify(token: UUID): String {
        userService.findByVerifyToken(token).let { user ->
            if (!user.isVerified && user.dateJoined.isAfter(LocalDateTime.now().minusDays(1))) {
                userService.updateOne(user.apply { isVerified = true })
            }

            return Templates.verify(user).render()
        }
    }

    @POST
    @Path("/auth/password/reset")
    @PermitAll
    @Transactional
    fun reset(@Valid payload: RequestResetDto, context: RoutingContext) {
        Pair(userService.findByMail(payload.mail), UUID.randomUUID()).let { (user, token) ->
            userService.updateOne(user.apply { resetToken = token })

            mailer.send(user.mail, "Password reset | mood", Templates.reset(user, context, token).render()).await()
                .indefinitely()
        }
    }

    @GET
    @Path("/auth/password/reset/{token}")
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.TEXT_PLAIN)
    @PermitAll
    fun resetHtml(token: UUID): String {
        userService.findByResetToken(token).let { user ->
            return Templates.resetForm(user).render()
        }
    }

    @PUT
    @Path("/auth/password/reset/{token}")
    @Produces(MediaType.TEXT_HTML)
    @PermitAll
    @Transactional
    fun reset(@Valid payload: ResetDto, token: UUID): String {
        if (payload.password != payload.passwordRepeat) {
            throw WebApplicationMapperException("Passwords do not match", 422)
        }

        userService.findByResetToken(token).let { user ->
            userService.updateOne(user.apply {
                password = hashService.hash(payload.password)
                resetToken = null
            })
        }

        return Templates.success().render()
    }
}
