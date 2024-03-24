package dev.wigger.mood.security

import dev.wigger.mood.model.Users
import io.smallrye.jwt.build.Jwt
import jakarta.enterprise.context.ApplicationScoped
import java.time.Instant
import java.time.temporal.ChronoUnit

@ApplicationScoped
class TokenService {
    /**
     * creates the jwt token
     * @param users user object that contains details e.g. username or user id
     * @return jwt with claimed values. signed by the *.pem keys in resources folder.
     */
    fun createToken(users: Users): String {
        return Jwt.claims().subject(users.username)
            .claim("userId", users.id)
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plus(30L, ChronoUnit.DAYS))
            .sign()
    }
}