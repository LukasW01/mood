package dev.wigger.mood.security

import io.quarkus.elytron.security.common.BcryptUtil
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class HashService {
    /**
     * Hashes a $string with BCrypt
     * @param password
     * @return hashed string
     */
    fun hash(password: String): String = BcryptUtil.bcryptHash(password)

    /**
     * Checks whether the string matches the hash
     * @param password
     * @param hash
     * @return true if hash(input) == hash, otherwise false
     */
    fun isHashedCrypt(password: String, hash: String): Boolean = BcryptUtil.matches(password, hash)
}
