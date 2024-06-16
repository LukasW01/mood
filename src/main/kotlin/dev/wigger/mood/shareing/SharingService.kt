package dev.wigger.mood.shareing

import dev.wigger.mood.dto.SharingDelegatorDto
import dev.wigger.mood.dto.toDto
import dev.wigger.mood.util.mapper.WebApplicationMapperException

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject

@ApplicationScoped
class SharingService {
    @Inject
    private lateinit var sharingRepository: SharingRepository
    
    fun findByUserId(userId: Long): List<Sharing> = sharingRepository.findByUserId(userId) ?: throw WebApplicationMapperException("No user found", 404)
    
    fun persistOne(sharing: Sharing) = sharingRepository.persistOne(sharing)
    
    fun findByUserIdAndDelegatorIdException(userId: Long, delegatorId: Long): Nothing? = sharingRepository.findByUserIdAndDelegatorId(userId, delegatorId)?.let {
        throw WebApplicationMapperException("Cannot connect a user twice", 422)
    }
    
    fun mapToDto(sharing: Sharing): SharingDelegatorDto = SharingDelegatorDto(
        createdAt = sharing.createdAt, updatedAt = sharing.updatedAt, delegator = sharing.delegator.toDto(),
    )
}
