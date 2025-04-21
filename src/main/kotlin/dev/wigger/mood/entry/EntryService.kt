package dev.wigger.mood.entry

import dev.wigger.mood.util.enums.Permissions
import dev.wigger.mood.util.mapper.WebApplicationMapperException
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import java.time.LocalDate
import java.util.UUID

@ApplicationScoped
class EntryService {
    @Inject
    private lateinit var entryRepository: EntryRepository

    fun persistOne(entry: Entry) = entryRepository.persistOne(entry)

    fun deleteById(id: Long) = entryRepository.deleteById(id)

    fun updateOne(entry: Entry) = entryRepository.updateOne(entry)

    fun findByUserId(userId: UUID): List<Entry> = entryRepository.findByUserId(userId)
        ?: throw WebApplicationMapperException("Entry does not exist", 404)

    fun findByIdAndUserId(id: Long, userId: UUID): Entry = entryRepository.findByIdAndUserId(id, userId)
        ?: throw WebApplicationMapperException("Entry does not exist", 404)

    fun findByUuidAndDateExists(userId: UUID, date: List<LocalDate>): MutableList<Entry> = entryRepository.findByUuidAndDate(userId, date).takeIf { it.isNullOrEmpty() }
        ?: throw WebApplicationMapperException("An entry already exists on this day", 422)

    fun findByIdPermission(userId: UUID, permissions: Permissions): List<Entry>? = when (permissions) {
        Permissions.ALL ->
            entryRepository.findByUserId(userId)
        Permissions.NO_JOURNAL ->
            entryRepository.findByUserId(userId)?.map { entry -> entry.apply { journal = null } }
        Permissions.HISTORY_LIMITED ->
            entryRepository.findByUuidForLastSevenDays(userId)
        Permissions.JOURNAL_HISTORY ->
            entryRepository.findByUuidForLastSevenDays(userId)?.map { entry -> entry.apply { journal = null } }
    }
}
