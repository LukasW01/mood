package dev.wigger.mood.entry

import dev.wigger.mood.util.mapper.WebApplicationMapperException
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import java.util.*

@ApplicationScoped
class EntryService {
    @Inject
    private lateinit var entryRepository: EntryRepository

    fun findByUserId(userId: Long): List<Entry> = entryRepository.findByUserId(userId) ?: throw WebApplicationMapperException("No Entry found", 400)
    
    fun findByUserIdEmpty(userId: Long): List<Entry>? = entryRepository.findByUserId(userId)

    fun findByIdAndUserId(id: UUID, userId: Long): Entry = entryRepository.findByIdAndUserId(id, userId)
        ?: throw WebApplicationMapperException("No Entry found", 400)
    
    fun findEntityByIdAndUserId(id: UUID, userId: Long): Entry = entryRepository.findByIdAndUserId(id, userId)
        ?: throw WebApplicationMapperException("No Entry found", 400)
    
    fun updateOne(id: UUID, entry: Entry) = entryRepository.updateOne(id, entry)

    fun persistOne(entry: Entry) = entryRepository.persistOne(entry)

    fun deleteById(id: UUID) = entryRepository.delete(id)
}
