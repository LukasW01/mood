package dev.wigger.mood.service

import dev.wigger.mood.model.Users
import dev.wigger.mood.repository.UserRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.persistence.EntityManager

@ApplicationScoped
class UserService {
    @Inject
    lateinit var userRepository: UserRepository

    fun persistOne(users: Users): Pair<Unit, Users> = userRepository.persistOne(users)

    fun deleteByUserId(id: Long) =  userRepository.deleteByUserId(id)

    fun findByIdOrNull(id: Long): Users? = userRepository.findByIdOrNull(id)

    fun findByUsername(name: String): Users? = userRepository.findByUsername(name)
    
}