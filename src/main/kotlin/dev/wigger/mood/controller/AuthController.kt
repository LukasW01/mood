package dev.wigger.mood.controller

import dev.wigger.mood.dto.*
import dev.wigger.mood.mail.Mailgun
import dev.wigger.mood.user.Users
import dev.wigger.mood.security.HashService
import dev.wigger.mood.security.TokenService
import dev.wigger.mood.user.UserService

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
import jakarta.ws.rs.core.SecurityContext
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme

/**
 * Reference for SecurityContext:
 * https://developer.okta.com/blog/2019/09/30/java-quarkus-oidc
 */
@Path("/api/v1") @ApplicationScoped @Produces("application/json") @Consumes("application/json") @SecurityScheme(
    scheme = "bearer",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
)
class AuthController {
    @Inject
    private lateinit var tokenService: TokenService

    @Inject
    private lateinit var hashService: HashService

    @Inject
    private lateinit var userService: UserService
    
    @Inject
    private lateinit var mailgun: Mailgun

    @Location("login.html")
    private lateinit var loginTemplate: Template
    
    @Location("register.html")
    private lateinit var registerTemplate: Template

    @Inject
    private lateinit var context: RoutingContext

    @POST @Path("/auth/login") @PermitAll @Transactional
    fun login(@Valid payload: LoginDto): AuthResponseDto {
        val user = userService.findByUsername(payload.username) ?: throw WebApplicationException("Login failed", 403)
        if (!hashService.isHashedPassword(payload.password, user.password)) {
            Log.warn("Login failed. The password entered is incorrect.")
            throw WebApplicationException("Login failed", 403)
        }

        mailgun.sendMessage(
            mailgun.buildMessage(user.mail, "Login", loginTemplate.data(mapOf("ip" to context.request().remoteAddress().host(), "user" to user)).render())
        )
        
        Log.info("Login successful. Returning token and user data of LoginResponseDto")
        return AuthResponseDto(
            token = tokenService.createToken(user),
        )
    }
    
    @POST @Path("/auth/register") @PermitAll @Transactional
    fun register(@Valid payload: RegisterDto) {
        userService.findByUsername(payload.username)?.let {
            Log.warn("Registration failed. The username is already taken.")
            throw WebApplicationException("Registration failed", 400)
        }

        mailgun.sendMessage(
            mailgun.buildMessage(payload.mail, "Register", registerTemplate.data(mapOf("ip" to context.request().remoteAddress().host(), "user" to payload)).render())
        )
        
        Log.info("Registering user with username: '${payload.username}'")
        userService.persistOne(Users().apply {
            username = payload.username
            mail = payload.mail
            firstName = payload.firstName
            lastName = payload.lastName
            password = hashService.hashPassword(payload.password)
        })

    }
    
    @PUT @Path("/auth/update") @Authenticated @Transactional
    fun update(@Valid payload: UpdateDto, ctx: SecurityContext) {
        val user = userService.findByUsername(ctx.userPrincipal.name) ?: throw WebApplicationException("User not found", 400)
        if (!hashService.isHashedPassword(payload.oldPassword, user.password)) {
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
                password = hashService.hashPassword(payload.newPassword)
            },
        )
    }
    
    @DELETE @Path("/auth/delete") @Authenticated @Transactional
    fun delete(@Valid payload: DeleteDto, ctx: SecurityContext) {
        val user = userService.findByUsername(ctx.userPrincipal.name) ?: throw WebApplicationException("User not found", 400)
        if (!hashService.isHashedPassword(payload.password, user.password)) {
            Log.warn("Login failed. The password entered is incorrect.")
            throw WebApplicationException("Login failed", 403)
        }
        
        Log.info("Deleting user data from the database")
        userService.deleteByUsername(payload.username)
    }
}
