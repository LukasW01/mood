package dev.wigger.mood.user

import io.quarkus.hibernate.orm.panache.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped
import java.time.LocalDateTime
import java.util.UUID

@ApplicationScoped
class UserRepository : PanacheRepository<Users> {
    fun findByMail(mail: String): Users? = find("mail = ?1", mail).firstResult()

    fun findByVerifyToken(token: UUID): Users? = find("verifyToken = ?1", token).firstResult()

    fun findByResetToken(token: UUID): Users? = find("resetToken = ?1", token).firstResult()
    
    fun findBySharingToken(token: UUID): Users? = find("sharingToken = ?1", token).firstResult()

    fun persistOne(users: Users) = persistAndFlush(users)
    
    fun deleteByMail(mail: String) = delete("mail = ?1", mail)
    
    fun deleteUnverifiedAndOldUsers() = delete("isVerified = ?1 and dateJoined < ?2", false, LocalDateTime.now().minusDays(1))

    fun updateTokenToNull(resetToken: UUID? = null, sharingToken: UUID? = null) = update("resetToken = ?1 and sharingToken = ?2", resetToken, sharingToken)

    fun findByLongId(id: Long): Users? = find("id = ?1", id).firstResult()

    fun updateOne(id: Long, users: Users) {
        findByLongId(id)?.apply {
            mail = users.mail
            lastName = users.lastName
            firstName = users.firstName
            password = users.password
            isVerified = users.isVerified
            persistAndFlush(this)
        }
    }
}
