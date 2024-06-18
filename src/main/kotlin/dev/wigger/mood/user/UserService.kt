package dev.wigger.mood.user

import dev.wigger.mood.util.mapper.WebApplicationMapperException
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import java.util.UUID

@ApplicationScoped
class UserService {
    @Inject
    private lateinit var userRepository: UserRepository

    fun persistOne(users: Users) = userRepository.persistOne(users)

    fun deleteByMail(mail: String) = userRepository.deleteByMail(mail)

    fun updateOne(id: Long, users: Users) = userRepository.updateOne(id, users)

    fun findByMail(mail: String): Users = userRepository.findByMail(mail) ?: throw WebApplicationMapperException("User does not exist", 404)
    
    fun findByVerifyToken(token: UUID): Users = userRepository.findByVerifyToken(token) ?: throw WebApplicationMapperException("User does not exist", 404)

    fun findByResetToken(token: UUID): Users = userRepository.findByResetToken(token) ?: throw WebApplicationMapperException("User does not exist", 404)

    fun findBySharingToken(token: UUID): Users = userRepository.findBySharingToken(token) ?: throw WebApplicationMapperException("User does not exist", 404)
    
    fun findByMailException(mail: String): Users? = userRepository.findByMail(mail)?.let { throw WebApplicationMapperException("User already exists", 422) }
}
