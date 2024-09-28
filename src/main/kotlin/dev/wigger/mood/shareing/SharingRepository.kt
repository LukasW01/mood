package dev.wigger.mood.shareing

import io.quarkus.hibernate.orm.panache.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped
import java.util.UUID

@ApplicationScoped
class SharingRepository : PanacheRepository<Sharing> {
    fun persistOne(sharing: Sharing) = persistAndFlush(sharing)

    fun delete(userId: UUID, delegatorId: UUID) = delete("user.id = ?1 and delegator.id = ?2", userId, delegatorId)
    
    fun findByUserId(userId: UUID): List<Sharing>? = find("user.id = ?1", userId).list()
    
    fun findByUserIdAndDelegatorId(userId: UUID, delegatorId: UUID): Sharing? = find("user.id = ?1 and delegator.id = ?2", userId, delegatorId).firstResult()

    fun updateOne(
        userId: UUID,
        delegatorId: UUID,
        sharing: Sharing,
    ) {
        findByUserIdAndDelegatorId(userId, delegatorId)?.apply {
            permissions = sharing.permissions
            persistAndFlush(this)
        }
    }
}
