package dev.wigger.mood.shareing

import io.quarkus.hibernate.orm.panache.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class SharingRepository : PanacheRepository<Sharing> {
    fun findByUserId(userId: Long): List<Sharing>? = find("user.id = ?1", userId).list()
    
    fun findByUserIdAndDelegatorId(userId: Long, delegatorId: Long): Sharing? = find("user.id = ?1 and delegator.id = ?2", userId, delegatorId).firstResult()

    fun persistOne(sharing: Sharing) = persistAndFlush(sharing)
}
