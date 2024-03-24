package dev.wigger.mood.resource

import dev.wigger.mood.dto.LoginDto
import dev.wigger.mood.dto.AuthResponseDto
import dev.wigger.mood.dto.RegisterDto
import dev.wigger.mood.dto.toDto
import dev.wigger.mood.model.Users
import dev.wigger.mood.security.HashService
import dev.wigger.mood.security.TokenService
import dev.wigger.mood.service.UserService
import jakarta.annotation.security.PermitAll
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.ws.rs.*
import io.quarkus.logging.Log
import jakarta.transaction.Transactional


@Path("/api/v1") @ApplicationScoped @Produces("application/json")
class AuthResource {
    @Inject
    private lateinit var tokenService: TokenService
    @Inject
    private lateinit var hashService: HashService
    @Inject
    private lateinit var userService: UserService
    
    @POST @Path("/auth/login") @PermitAll @Transactional @Consumes("application/json")
    fun login(payload: LoginDto): AuthResponseDto {
        val user = userService.findByUsername(payload.username) ?: throw WebApplicationException("Login failed", 404) 
        if(!hashService.verifyPassword(payload.password, user.password)) {
            Log.warn("Login failed. The password entered is incorrect.")
            throw WebApplicationException("Login failed", 403)
        }
        
        Log.info("Login successful. Returning token and user data of LoginResponseDto")
        return AuthResponseDto(
            token = tokenService.createToken(user),
            user = user.toDto()
        )
    }
    
    @POST @Path("/auth/register") @PermitAll @Transactional @Consumes("application/json")
    fun register(payload: RegisterDto): AuthResponseDto {
        if(userService.findByUsername(payload.username) != null) {
            Log.warn("Registration failed. The username is already taken.")
            throw WebApplicationException("Registration failed", 409)
        }
        
        Log.info("Registering user with username: '${payload.username}'")
        val user = userService.persistOne(Users().apply {
            username = payload.username
            mail = payload.mail
            firstName = payload.firstName
            lastName = payload.lastName
            password = hashService.hashPassword(payload.password)
        })
        
        Log.info("Registration successful. Returning token and user data of LoginResponseDto")
        return AuthResponseDto(
            token = tokenService.createToken(user.second),
            user = user.second.toDto()
        )
    }
    


}