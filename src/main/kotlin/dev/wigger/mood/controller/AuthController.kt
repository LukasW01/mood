package dev.wigger.mood.controller

import dev.wigger.mood.dto.*
import dev.wigger.mood.mail.Mailgun
import dev.wigger.mood.security.HashService
import dev.wigger.mood.security.TokenService
import dev.wigger.mood.user.UserService
import dev.wigger.mood.user.Users
import io.quarkiverse.bucket4j.runtime.RateLimited
import io.quarkiverse.bucket4j.runtime.resolver.IpResolver
import io.quarkus.logging.Log
import io.quarkus.qute.Location
import io.quarkus.qute.Template
import io.quarkus.security.Authenticated
import io.vertx.ext.web.RoutingContext
import jakarta.annotation.security.PermitAll
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.SecurityContext
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme
import java.time.LocalDateTime
import java.util.*

/**
 * Reference for SecurityContext:
 * https://developer.okta.com/blog/2019/09/30/java-quarkus-oidc
 */
@ApplicationScoped
@Path("/v1")@Produces(MediaType.APPLICATION_JSON) @Consumes(MediaType.APPLICATION_JSON)
@SecurityScheme(scheme = "bearer", type = SecuritySchemeType.HTTP, bearerFormat = "JWT")
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

    @Inject
    private lateinit var context: RoutingContext

    @Location("login.html")
    private lateinit var loginTemplate: Template
    
    @Location("register.html")
    private lateinit var registerTemplate: Template
    
    @Location("verified.html")
    private lateinit var verifiedTemplate: Template

    @Inject @ConfigProperty(name = "domain")
    private lateinit var domain: String

    @POST @Path("/auth/login") @PermitAll @Transactional
    fun login(@Valid payload: LoginDto): AuthResponseDto {
        val user = userService.findByUsername(payload.username)
        if (!hashService.isHashedArgon(payload.password, user.password)) {
            Log.warn("Login failed. The password entered is incorrect.")
            throw WebApplicationException("Login failed", 403)
        }

        mailgun.sendMessage(
            mailgun.buildMessage(user.mail, "Login",
                loginTemplate.data(mapOf("ip" to context.request().remoteAddress().host(), "user" to user)).render()),
        )
        
        Log.info("Login successful. Returning token and user data of LoginResponseDto")
        return AuthResponseDto(
            token = tokenService.createToken(user),
            user = user.toDto(),
        )
    }
    
    @POST @Path("/auth/register") @PermitAll @Transactional
    fun register(@Valid payload: RegisterDto) {
        userService.findByUsernameOrMail(payload.username, payload.mail)?.let { throw WebApplicationException("Constraint violation", 400) }
        
        Log.info("Registering user with username: '${payload.username}'")
        val user = userService.persistOne(Users().apply {
            username = payload.username
            mail = payload.mail
            firstName = payload.firstName
            lastName = payload.lastName
            password = hashService.hashArgon(payload.password)
            token = UUID.randomUUID()
            dateJoined = LocalDateTime.now()
            isVerified = false
        })

        mailgun.sendMessage(
            mailgun.buildMessage(payload.mail, "Register", registerTemplate.data(mapOf("ip" to context.request().remoteAddress().host(),
                "user" to payload, "link" to "${domain.replaceFirst("/*$", "")}/v1/auth/verify/${user.token}")).render()),
        )
    }

    @GET @Path("/auth/verify/{token}") @PermitAll @Transactional @Produces(MediaType.TEXT_HTML)
    fun register(token: UUID): String {
        val user = userService.findByToken(token)
        
        return when {
            !user.isVerified && user.dateJoined.isAfter(LocalDateTime.now().minusDays(1)) -> {
                userService.updateOne(user.id, user.apply { isVerified = true })
        
                mailgun.sendMessage(
                    mailgun.buildMessage(user.mail, "Account verified!", verifiedTemplate.data(mapOf("user" to user)).render()),
                )
        
                verifiedTemplate.data(mapOf("user" to user, "yesterday" to LocalDateTime.now().minusDays(1))).render()
            }
            else -> verifiedTemplate.data(mapOf("user" to user, "yesterday" to LocalDateTime.now().minusDays(1))).render()
        }
    }

    @PUT @Path("/auth/update") @Authenticated @Transactional
    fun update(@Valid payload: UpdateDto, ctx: SecurityContext) {
        val user = userService.findByUsername(ctx.userPrincipal.name)
        if (!hashService.isHashedArgon(payload.oldPassword, user.password)) {
            Log.warn("Login failed. The password entered is incorrect.")
            throw WebApplicationException("Login failed", 403)
        }
        
        Log.info("Updating user data in the database")
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
    
    @DELETE @Path("/auth/delete") @Authenticated @Transactional
    fun delete(@Valid payload: DeleteDto, ctx: SecurityContext) {
        val user = userService.findByUsername(ctx.userPrincipal.name)
        if (!hashService.isHashedArgon(payload.password, user.password)) {
            Log.warn("Login failed. The password entered is incorrect.")
            throw WebApplicationException("Login failed", 403)
        }
        
        Log.info("Deleting user data from the database")
        userService.deleteByUsername(payload.username)
    }
}
