package dev.wigger.mood.dto

import dev.wigger.mood.util.annotation.MoodEnum
import java.util.Date

data class EntryDto(
    var mood: String,
    var journal: String,
    var date: Date,
    var color: String,
)

data class EntrySaveDto(
    var mood: String,
    var journal: String,
    var date: Date,
    var color: String,
)

data class EntryUpdateDto(
    @MoodEnum
    var mood: String?,
    var journal: String?,
    var date: Date?,
    var color: String?,
)
