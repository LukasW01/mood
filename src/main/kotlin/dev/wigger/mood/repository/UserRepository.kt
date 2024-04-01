package dev.wigger.mood.repository

import dev.wigger.mood.model.Users
import io.quarkus.hibernate.orm.panache.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class UserRepository : PanacheRepository<Users> {
    fun persistOne(users: Users): Pair<Unit, Users> = persistAndFlush(users) to users
    fun deleteByUsername(name: String) = delete("username = ?1", name)
    fun findByUsername(username: String): Users? = find("username = ?1", username).firstResult()
    fun findByID(id: Long): Users? = find("id = ?1", id).firstResult()
    fun updateOne(id: Long, users: Users) {
        findByID(id)?.apply {
            username = users.username
            mail = users.mail
            lastName = users.lastName
            firstName = users.firstName
            password = users.password
            persistAndFlush(this)
        }
    }
}
