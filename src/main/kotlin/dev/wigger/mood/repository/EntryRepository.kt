package dev.wigger.mood.repository

import dev.wigger.mood.model.Entry
import io.quarkus.hibernate.orm.panache.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class EntryRepository : PanacheRepository<Entry> {
    fun findByUserId(userId: Long): List<Entry>? = find("user.id = ?1", userId).list()
    fun findByIdAndUserId(id: Long, userId: Long): Entry? = find("id = ?1 and user.id = ?2", id, userId).firstResult()
    fun persistOne(entry: Entry) = persistAndFlush(entry)
    fun delete(id: Long) = delete("id = ?1", id)
    fun findByID(id: Long): Entry? = find("id = ?1", id).firstResult()
    fun updateOne(id: Long, entry: Entry) {
        findByID(id)?.apply {
            mood = entry.mood
            journal = entry.journal
            date = entry.date
            color = entry.color
            persistAndFlush(this)
        }
    }
}
