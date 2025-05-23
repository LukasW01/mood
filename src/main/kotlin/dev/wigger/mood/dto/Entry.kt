package dev.wigger.mood.dto

import dev.wigger.mood.entry.Entry
import jakarta.json.bind.annotation.JsonbCreator
import java.time.LocalDate

data class EntryDto @JsonbCreator constructor(
    var id: Long,
    var mood: String,
    var journal: String?,
    val date: LocalDate,
    var color: String,
)

data class EntrySubmitDto @JsonbCreator constructor(
    var mood: String,
    var journal: String,
    val date: LocalDate,
    var color: String,
)

data class EntryUpdateDto @JsonbCreator constructor(
    var mood: String?,
    var journal: String?,
    val date: LocalDate?,
    var color: String?,
)

fun List<Entry>.toDtoList(): List<EntryDto> = this.map { it.toDto() }

fun Entry.toDto(): EntryDto = EntryDto(
    id = id,
    mood = mood,
    journal = journal,
    date = date,
    color = color,
)
