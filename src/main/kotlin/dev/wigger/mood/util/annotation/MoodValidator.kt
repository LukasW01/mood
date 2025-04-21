package dev.wigger.mood.util.annotation

import dev.wigger.mood.util.enums.Moods
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class MoodValidator : ConstraintValidator<Mood, String> {
    override fun isValid(value: String, context: ConstraintValidatorContext?): Boolean =
        Moods.entries.any { it.name == value }
}
