package dev.wigger.mood.security

import dev.wigger.mood.user.Users
import dev.wigger.mood.util.enums.Roles
import io.smallrye.jwt.build.Jwt
import jakarta.enterprise.context.ApplicationScoped
import java.time.Instant
import java.time.temporal.ChronoUnit

@ApplicationScoped
class TokenService {
    /**
     * creates the jwt token
     * @param users user object that contains details e.g. username or user id
     * @param groups role enum for any Role based authentication
     * @return jwt with claimed values. signed by the *.pem keys in resources folder.
     */
    fun createToken(users: Users, groups: Roles): String = Jwt.claims()
        .subject(users.id.toString())
        .groups(groups.toString())
        .issuedAt(Instant.now())
        .expiresAt(Instant.now().plus(30L, ChronoUnit.DAYS))
        .sign()
}
