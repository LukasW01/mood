package dev.wigger.mood.user

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.ws.rs.WebApplicationException
import java.util.UUID

@ApplicationScoped
class UserService {
    @Inject
    private lateinit var userRepository: UserRepository

    fun persistOne(users: Users): Users = userRepository.persistOne(users).run { findByUsername(users.username) }

    fun deleteByUsername(name: String) = userRepository.deleteByUsername(name)

    fun findByUsername(name: String): Users = userRepository.findByUsername(name) ?: throw WebApplicationException("User does not exist", 403)
    
    fun findByUsernameOrMail(username: String, mail: String): Users? = userRepository.findByUsernameOrMail(username, mail)?.let { throw WebApplicationException("Constraint violation", 400) }
    
    fun findByMail(mail: String): Users = userRepository.findByMail(mail) ?: throw WebApplicationException("User does not exist", 404)
    
    fun findByToken(token: UUID): Users = userRepository.findByToken(token) ?: throw WebApplicationException("User does not exist", 404)

    fun findByResetToken(token: UUID): Users = userRepository.findByResetToken(token) ?: throw WebApplicationException("User does not exist", 404)
    
    fun updateOne(id: Long, users: Users) = userRepository.updateOne(id, users)
}
