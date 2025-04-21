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
    
    fun deleteByUserUuid(userId: UUID, delegatorId: UUID) = sharingRepository.deleteByUserUuid(userId, delegatorId)

    fun updateOne(sharing: Sharing) = sharingRepository.updateOne(sharing)

    fun findByUserUuid(userId: UUID): List<Sharing> = sharingRepository.findByUserUuid(userId)
        ?: throw WebApplicationMapperException("User does not exist", 404)

    fun findByUserAndDelegator(userId: UUID, delegatorId: UUID) = sharingRepository.findByUserAndDelegator(userId, delegatorId)
        ?: throw WebApplicationMapperException("User does not exist", 404)

    fun findByUserAndDelegatorExists(userId: UUID, delegatorId: UUID) = sharingRepository.findByUserAndDelegator(userId, delegatorId)?.let {
        throw WebApplicationMapperException("Cannot connect a user twice", 422)
    }
}
