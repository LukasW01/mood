package dev.wigger.mood.dto

import jakarta.json.bind.annotation.JsonbCreator
import java.time.LocalDate
import java.util.UUID

data class EntryDto @JsonbCreator constructor(
    var id: UUID,
    var mood: String,
    var journal: String?,
    val date: LocalDate,
    var color: String,
)

data class EntrySaveDto @JsonbCreator constructor(
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
