package dev.wigger.mood.security

import de.mkammerer.argon2.Argon2Factory
import jakarta.enterprise.context.ApplicationScoped
import org.mindrot.jbcrypt.BCrypt

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
    fun hashArgon(password: String): String = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id, 32, 64).hash(22, 65_536, 1,
        password.toCharArray())

    fun hashBCrypt(secret: String): String = BCrypt.hashpw(secret, BCrypt.gensalt(4))
    
    /**
     * checks whether the string matches the hash
     * @param password the string to be checked
     * @param hash the hashed string
     * @return true if hash(input) == hash, otherwise false
     */
    fun isHashedArgon(password: String, hash: String): Boolean = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id, 32, 64)
        .verify(hash, password.toCharArray())
    
    fun isHashBCrypt(secret: String, hash: String): Boolean = BCrypt.checkpw(secret, hash)
}
