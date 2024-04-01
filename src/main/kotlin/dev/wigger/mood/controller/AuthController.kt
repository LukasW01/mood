package dev.wigger.mood.controller

import dev.wigger.mood.dto.*
import dev.wigger.mood.model.Users
import dev.wigger.mood.security.HashService
import dev.wigger.mood.security.TokenService
import dev.wigger.mood.service.UserService
import jakarta.annotation.security.PermitAll
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.ws.rs.*
import io.quarkus.logging.Log
import io.quarkus.security.Authenticated
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import jakarta.ws.rs.core.SecurityContext



/**
 * Reference for SecurityContext:
 * https://developer.okta.com/blog/2019/09/30/java-quarkus-oidc
 */
@Path("/api/v1") @ApplicationScoped @Produces("application/json") @Consumes("application/json")
class AuthController {
    @Inject
    private lateinit var tokenService: TokenService
    @Inject
    private lateinit var hashService: HashService
    @Inject
    private lateinit var userService: UserService

    @POST @Path("/auth/login") @PermitAll @Transactional
    fun login(@Valid payload: LoginDto): AuthResponseDto {
        val user = userService.findByUsername(payload.username) ?: throw WebApplicationException("Login failed", 403)
        if(!hashService.verifyPassword(payload.password, user.password)) {
            Log.warn("Login failed. The password entered is incorrect.")
            throw WebApplicationException("Login failed", 403)
        }
        
        Log.info("Login successful. Returning token and user data of LoginResponseDto")
        return AuthResponseDto(
            token = tokenService.createToken(user),
        )
    }
    
    @POST @Path("/auth/register") @PermitAll @Transactional
    fun register(@Valid payload: RegisterDto) {
        if(userService.findByUsername(payload.username) != null) {
            Log.warn("Registration failed. The username is already taken.")
            throw WebApplicationException("Registration failed", 400)
        }
        
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
        if(!hashService.verifyPassword(payload.oldPassword, user.password)) {
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
            }
        )
    }
    
    @DELETE @Path("/auth/delete") @Authenticated @Transactional
    fun delete(@Valid payload: DeleteDto, ctx: SecurityContext) {
        val user = userService.findByUsername(ctx.userPrincipal.name) ?: throw WebApplicationException("User not found", 400)
        if(!hashService.verifyPassword(payload.password, user.password)) {
            Log.warn("Login failed. The password entered is incorrect.")
            throw WebApplicationException("Login failed", 403)
        }
        
        Log.info("Deleting user data from the database")
        userService.deleteByUsername(payload.username)
    }

}