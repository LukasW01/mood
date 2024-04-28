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
    
    fun findByUsernameOrMail(username: String, mail: String): Users? = userRepository.findByUsernameOrMail(username, mail)
    
    fun findByToken(token: UUID): Users = userRepository.findByToken(token) ?: throw WebApplicationException("User does not exist", 403)
    
    fun updateOne(id: UUID, users: Users) = userRepository.updateOne(id, users)
}
