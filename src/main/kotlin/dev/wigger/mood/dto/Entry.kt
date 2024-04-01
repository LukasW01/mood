package dev.wigger.mood.dto

import jakarta.json.bind.annotation.JsonbCreator
import java.util.Date

data class EntryDto @JsonbCreator constructor(
    var mood: String,
    var journal: String?,
    val date: Date,
    var color: String,
)

data class EntrySaveDto @JsonbCreator constructor(
    var mood: String,
    var journal: String,
    val date: Date,
    var color: String,
)

data class EntryUpdateDto @JsonbCreator constructor(
    var mood: String?,
    var journal: String?,
    val date: Date?,
    var color: String?,
)
