package dev.wigger.mood.user

import io.quarkus.hibernate.orm.panache.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped
import java.time.LocalDateTime
import java.util.UUID

@ApplicationScoped
class UserRepository : PanacheRepository<Users> {
    fun persistOne(users: Users) = persistAndFlush(users)
    
    fun deleteByUuid(uuid: UUID) = delete("id = ?1", uuid)
    
    fun deleteUnverifiedUsers() = delete("isVerified = ?1 and dateJoined < ?2", false, LocalDateTime.now().minusDays(1))

    fun updateResetTokenToNull() = update("resetToken = ?1", null as UUID?)

    fun updateSharingTokenToNull() = update("sharingToken = ?1", null as UUID?)

    fun findByMail(mail: String): Users? = find("mail = ?1", mail).firstResult()

    fun findByIdUuid(id: UUID): Users? = find("id = ?1", id).firstResult()

    fun findByVerifyToken(token: UUID): Users? = find("verifyToken = ?1", token).firstResult()

    fun findByResetToken(token: UUID): Users? = find("resetToken = ?1", token).firstResult()

    fun findBySharingToken(token: UUID): Users? = find("sharingToken = ?1", token).firstResult()

    fun updateOne(users: Users) {
        users.apply {
            mail = users.mail
            lastName = users.lastName
            firstName = users.firstName
            password = users.password
            isVerified = users.isVerified
            persistAndFlush(this)
        }
    }
}
