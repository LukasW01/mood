package dev.wigger.mood.repository

import dev.wigger.mood.model.Users
import io.quarkus.hibernate.orm.panache.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional

@ApplicationScoped
class UserRepository: PanacheRepository<Users> {
    @Transactional
    fun persistOne(users: Users): Pair<Unit, Users> = persistAndFlush(users) to users
    @Transactional
    fun deleteByUserId(id: Long) = delete("id = ?1", id)
    fun findByIdOrNull(userId: Long): Users? = find("id = ?1",userId).firstResult()
    fun findByUsername(username: String): Users? = find("username = ?1", username).firstResult()
}