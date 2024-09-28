package dev.wigger.mood.entry

import dev.wigger.mood.util.enums.Permissions
import dev.wigger.mood.util.mapper.WebApplicationMapperException
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import java.time.LocalDate
import java.util.*

@ApplicationScoped
class EntryService {
    @Inject
    private lateinit var entryRepository: EntryRepository

    fun updateOne(id: Long, entry: Entry) = entryRepository.updateOne(id, entry)

    fun persistOne(entry: Entry) = entryRepository.persistOne(entry)

    fun deleteById(id: Long) = entryRepository.delete(id)

    fun findByUserId(userId: UUID): List<Entry> = entryRepository.findByUserId(userId) ?: throw WebApplicationMapperException("No Entry found", 404)

    fun findByIdAndUserId(id: Long, userId: UUID): Entry = entryRepository.findByIdAndUserId(id, userId)
        ?: throw WebApplicationMapperException("No Entry found", 404)
    
    fun findEntityByIdAndUserId(id: Long, userId: UUID): Entry = entryRepository.findByIdAndUserId(id, userId)
        ?: throw WebApplicationMapperException("No Entry found", 404)

    fun findByUserIdAndDateException(userId: UUID, date: List<LocalDate>) {
        entryRepository.findByUserIdAndDate(userId, date).let {
            if (!it.isNullOrEmpty()) {
                throw WebApplicationMapperException("An entry already exists on this day", 422)
            }
        }
    }
    
    fun findByUserIdPermission(userId: UUID, permissions: Permissions): List<Entry>? = when (permissions) {
        Permissions.ALL ->
            entryRepository.findByUserId(userId)
        Permissions.NO_JOURNAL ->
            entryRepository.findByUserId(userId)?.map { entry -> entry.apply { journal = null } }
        Permissions.HISTORY_LIMITED ->
            entryRepository.findByUserIdForLastSevenDays(userId)
        Permissions.JOURNAL_HISTORY ->
            entryRepository.findByUserIdForLastSevenDays(userId)?.map { entry -> entry.apply { journal = null } }
    }
}
