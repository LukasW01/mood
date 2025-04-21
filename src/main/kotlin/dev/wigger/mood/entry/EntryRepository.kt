package dev.wigger.mood.entry

import io.quarkus.hibernate.orm.panache.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped
import java.time.LocalDate
import java.util.UUID

@ApplicationScoped
class EntryRepository : PanacheRepository<Entry> {
    fun persistOne(entry: Entry) = persistAndFlush(entry)

    fun findByUserId(userId: UUID): List<Entry>? = find("user.id = ?1", userId).list()

    fun findByIdAndUserId(id: Long, userId: UUID): Entry? = find("id = ?1 and user.id = ?2", id, userId).firstResult()

    fun findByUuidForLastSevenDays(userId: UUID): List<Entry>? = find("user.id = ?1 and date >= ?2 ", userId, LocalDate.now().minusDays(7)).list()

    fun findByUuidAndDate(userId: UUID, date: List<LocalDate>): MutableList<Entry>? = list("user.id = ?1 and date in ?2", userId, date)

    fun updateOne(entry: Entry) {
        entry.apply {
            mood = entry.mood
            journal = entry.journal
            date = entry.date
            color = entry.color
            persistAndFlush(this)
        }
    }
}
