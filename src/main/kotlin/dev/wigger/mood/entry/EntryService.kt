package dev.wigger.mood.entry

import dev.wigger.mood.dto.EntryDto
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import java.util.*

@ApplicationScoped
class EntryService {
    @Inject
    private lateinit var entryRepository: EntryRepository

    fun findByUserId(userId: UUID): List<EntryDto>? = entryRepository.findByUserId(userId)?.map { entry -> mapToDto(entry) }

    fun findByIdAndUserId(id: UUID, userId: UUID): EntryDto? = entryRepository.findByIdAndUserId(id, userId)?.let { entry -> mapToDto(entry) }
    
    fun findEntityByIdAndUserId(id: UUID, userId: UUID): Entry? = entryRepository.findByIdAndUserId(id, userId)
    
    fun updateOne(id: UUID, entry: Entry) = entryRepository.updateOne(id, entry)

    fun persistOne(entry: Entry) = entryRepository.persistOne(entry)

    fun deleteById(id: UUID) = entryRepository.delete(id)

    fun mapToDto(entry: Entry): EntryDto = EntryDto(
        id = entry.id,
        mood = entry.mood,
        journal = entry.journal ?: "",
        date = entry.date,
        color = entry.color,
    )
}
