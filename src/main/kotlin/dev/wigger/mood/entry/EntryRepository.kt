package dev.wigger.mood.entry

import io.quarkus.hibernate.orm.panache.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped
import java.util.UUID

@ApplicationScoped
class EntryRepository : PanacheRepository<Entry> {
    fun findByUserId(userId: Long): List<Entry>? = find("user.id = ?1", userId).list()

    fun findByIdAndUserId(id: UUID, userId: Long): Entry? = find("id = ?1 and user.id = ?2", id, userId).firstResult()

    fun persistOne(entry: Entry) = persistAndFlush(entry)

    fun delete(id: UUID) = delete("id = ?1", id)

    fun findById(id: UUID): Entry? = find("id = ?1", id).firstResult()

    fun updateOne(id: UUID, entry: Entry) {
        findByID(id)?.apply {
            mood = entry.mood
            journal = entry.journal
            date = entry.date
            color = entry.color
            persistAndFlush(this)
        }
    }
}
