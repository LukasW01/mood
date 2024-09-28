package dev.wigger.mood.shareing

import dev.wigger.mood.util.mapper.WebApplicationMapperException

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import java.util.UUID

@ApplicationScoped
class SharingService {
    @Inject
    private lateinit var sharingRepository: SharingRepository

    fun persistOne(sharing: Sharing) = sharingRepository.persistOne(sharing)
    
    fun delete(userId: UUID, delegatorId: UUID) = sharingRepository.delete(userId, delegatorId)

    fun findByUserId(userId: UUID): List<Sharing> = sharingRepository.findByUserId(userId)
        ?: throw WebApplicationMapperException("No user found", 404)
    
    fun findByUserIdAndDelegatorId(userId: UUID, delegatorId: UUID): Sharing? = sharingRepository.findByUserIdAndDelegatorId(userId, delegatorId)

    fun updateOne(
        userId: UUID,
        delegatorId: UUID,
        sharing: Sharing,
    ) = sharingRepository.updateOne(userId, delegatorId, sharing)
}
