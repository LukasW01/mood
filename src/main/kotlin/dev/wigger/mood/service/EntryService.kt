package dev.wigger.mood.service

import dev.wigger.mood.dto.EntryDto
import dev.wigger.mood.dto.UserDto
import dev.wigger.mood.model.Entry
import dev.wigger.mood.model.Users
import dev.wigger.mood.repository.EntryRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.json.bind.annotation.JsonbCreator
import java.util.*


@ApplicationScoped
class EntryService{
    @Inject
    private lateinit var entryRepository: EntryRepository

    fun findByUserId(userId: Long): List<EntryDto>? = entryRepository.findByUserId(userId)?.map { entry -> mapToDto(entry) }

    fun findByIdAndUserId(id: Long, userId: Long): EntryDto? = entryRepository.findByIdAndUserId(id, userId)?.let { entry -> mapToDto(entry) }
    
    fun findEntityByIdAndUserId(id: Long, userId: Long): Entry? = entryRepository.findByIdAndUserId(id, userId)
    
    fun updateOne(id: Long, entry: Entry) = entryRepository.updateOne(id, entry)

    fun persistOne(entry: Entry) = entryRepository.persistOne(entry)

    fun deleteById(id: Long) = entryRepository.delete(id)

    fun mapToDto(entry: Entry): EntryDto = EntryDto(
        mood = entry.mood, 
        journal = entry.journal ?: "", 
        date = entry.date, 
        color = entry.color,
    )
}
