package dev.wigger.mood.shareing

import io.quarkus.hibernate.orm.panache.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped
import java.util.UUID

@ApplicationScoped
class SharingRepository : PanacheRepository<Sharing> {
    fun persistOne(sharing: Sharing) = persistAndFlush(sharing)

    fun deleteByUserUuid(userId: UUID, delegatorId: UUID) = delete("user.id = ?1 and delegator.id = ?2", userId, delegatorId)
    
    fun findByUserUuid(userId: UUID): List<Sharing>? = find("user.id = ?1", userId).list()
    
    fun findByUserAndDelegator(userId: UUID, delegatorId: UUID): Sharing? = find("user.id = ?1 and delegator.id = ?2", userId, delegatorId).firstResult()

    fun updateOne(sharing: Sharing) {
        sharing.apply {
            permissions = sharing.permissions
            persistAndFlush(this)
        }
    }
}
