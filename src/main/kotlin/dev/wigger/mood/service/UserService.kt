package dev.wigger.mood.service

import dev.wigger.mood.model.Users
import dev.wigger.mood.repository.UserRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject

@ApplicationScoped
class UserService {
    @Inject
    private lateinit var userRepository: UserRepository

    fun persistOne(users: Users) = userRepository.persistOne(users)

    fun deleteByUsername(name: String) = userRepository.deleteByUsername(name)

    fun findByUsername(name: String): Users? = userRepository.findByUsername(name)
    
    fun updateOne(id: Long, users: Users) = userRepository.updateOne(id, users)
}
