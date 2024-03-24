package dev.wigger.mood.service

import dev.wigger.mood.model.Entry
import dev.wigger.mood.repository.EntryRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject


@ApplicationScoped
class EntryService{
    @Inject
    private lateinit var entryRepository: EntryRepository

    fun updateOne(id: Long, entry: Entry) = entryRepository.updateOne(id, entry)

    fun persistOne(entry: Entry) = entryRepository.persistOne(entry)

    fun deleteById(id: Long) = entryRepository.delete(id)
    
    fun findByUserId(userId: Long): List<Entry>? = entryRepository.findByUserId(userId)
    
    fun findByIdAndUserId(id: Long, userId: Long): Entry? = entryRepository.findByIdAndUserId(id, userId)
}
