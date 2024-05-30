package dev.wigger.mood.user

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.ws.rs.WebApplicationException
import java.util.UUID

@ApplicationScoped
class UserService {
    @Inject
    private lateinit var userRepository: UserRepository

    fun persistOne(users: Users) = userRepository.persistOne(users)

    fun deleteByMail(mail: String) = userRepository.deleteByMail(mail)

    fun findByMail(mail: String): Users = userRepository.findByMail(mail) ?: throw WebApplicationException("User does not exist", 404)
    
    fun findByVerifyToken(token: UUID): Users = userRepository.findByVerifyToken(token) ?: throw WebApplicationException("User does not exist", 404)

    fun findByResetToken(token: UUID): Users = userRepository.findByResetToken(token) ?: throw WebApplicationException("User does not exist", 404)

    fun deleteUnverifiedAndOldUsers() = userRepository.deleteUnverifiedAndOldUsers()
    
    fun updateResetTokenToNull() = userRepository.updateResetTokenToNull()
    
    fun updateOne(id: Long, users: Users) = userRepository.updateOne(id, users)

    fun findByMailException(mail: String): Users? = userRepository.findByMail(mail)?.let {
        throw WebApplicationException("Constraint violation", 422)
    }
}
