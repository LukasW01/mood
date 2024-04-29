package dev.wigger.mood.user

import io.quarkus.hibernate.orm.panache.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped
import java.time.LocalDateTime
import java.util.UUID

@ApplicationScoped
class UserRepository : PanacheRepository<Users> {
    fun persistOne(users: Users) = persistAndFlush(users)
    
    fun deleteByUsername(name: String) = delete("username = ?1", name)
    
    fun findByUsername(username: String): Users? = find("username = ?1", username).firstResult()
    
    fun findByUsernameOrMail(username: String, mail: String): Users? = find("username = ?1 or mail = ?2", username, mail).firstResult()

    fun findByMail(mail: String): Users? = find("mail = ?1", mail).firstResult()

    fun findByVerifyToken(token: UUID): Users? = find("verifyToken = ?1", token).firstResult()
    
    fun findByResetToken(token: UUID): Users? = find("resetToken = ?1", token).firstResult()
    
    fun findByID(id: Long): Users? = find("id = ?1", id).firstResult()

    fun deleteUnverifiedAndOldUsers() = delete("isVerified = ?1 and dateJoined < ?2", false, LocalDateTime.now().minusDays(1))

    fun updateResetTokenToNull() = update("resetToken = ?1", null as UUID?)
    
    fun updateOne(id: Long, users: Users) {
        findByID(id)?.apply {
            username = users.username
            mail = users.mail
            lastName = users.lastName
            firstName = users.firstName
            password = users.password
            isVerified = users.isVerified
            persistAndFlush(this)
        }
    }
}