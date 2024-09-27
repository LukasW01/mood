package dev.wigger.mood.shareing

import dev.wigger.mood.util.mapper.WebApplicationMapperException

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject

@ApplicationScoped
class SharingService {
    @Inject
    private lateinit var sharingRepository: SharingRepository

    fun persistOne(sharing: Sharing) = sharingRepository.persistOne(sharing)

    fun updateOne(
        userId: Long,
        delegatorId: Long,
        sharing: Sharing,
    ) = sharingRepository.updateOne(userId, delegatorId, sharing)
    
    fun delete(userId: Long, delegatorId: Long) = sharingRepository.delete(userId, delegatorId)

    fun findByUserId(userId: Long): List<Sharing> = sharingRepository.findByUserId(userId)
        ?: throw WebApplicationMapperException("No user found", 404)
    
    fun findByUserIdAndDelegatorId(userId: Long, delegatorId: Long): Sharing? = sharingRepository.findByUserIdAndDelegatorId(userId, delegatorId)
}
