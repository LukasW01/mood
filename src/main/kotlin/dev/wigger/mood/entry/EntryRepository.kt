package dev.wigger.mood.entry

import io.quarkus.hibernate.orm.panache.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped
import java.time.LocalDate
import java.util.UUID

@ApplicationScoped
class EntryRepository : PanacheRepository<Entry> {
    fun persistOne(entry: Entry) = persistAndFlush(entry)

    fun delete(id: Long) = delete("id = ?1", id)

    fun findByUserId(userId: UUID): List<Entry>? = find("user.id = ?1", userId).list()

    fun findByUserIdForLastSevenDays(userId: UUID): List<Entry>? = find("user.id = ?1 and date >= ?2 ", userId, LocalDate.now().minusDays(7)).list()

    fun findByUserIdAndDate(userId: UUID, date: List<LocalDate>): MutableList<Entry>? = list("user.id = ?1 and date in ?2", userId, date)
    
    fun findByIdAndUserId(id: Long, userId: UUID): Entry? = find("id = ?1 and user.id = ?2", id, userId).firstResult()

    fun findByUuid(id: Long): Entry? = find("id = ?1", id).firstResult()

    fun updateOne(id: Long, entry: Entry) {
        findByUuid(id)?.apply {
            mood = entry.mood
            journal = entry.journal
            date = entry.date
            color = entry.color
            persistAndFlush(this)
        }
    }
}
