package dev.wigger.mood.security

import io.quarkus.elytron.security.common.BcryptUtil
import jakarta.enterprise.context.ApplicationScoped

/**
 * hashes the strings and checks them
 */
@ApplicationScoped
class HashService {
    /**
     * hashes the string
     * @param password the string to be hashed
     * @return the hashed string
     */
    fun hash(password: String): String = BcryptUtil.bcryptHash(password)

    /**
     * checks whether the string matches the hash
     * @param password the string to be checked
     * @param hash the hashed string
     * @return true if hash(input) == hash, otherwise false
     */
    fun isHashedCrypt(password: String, hash: String): Boolean = BcryptUtil.matches(password, hash)
}
