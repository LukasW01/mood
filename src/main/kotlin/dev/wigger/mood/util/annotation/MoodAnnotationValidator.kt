package dev.wigger.mood.util.annotation

import com.nulabinc.zxcvbn.Zxcvbn
import dev.wigger.mood.util.enums.Mood
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class MoodAnnotationValidator : ConstraintValidator<MoodAnnotation, String> {
    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        return value?.let {
            Mood.entries.any { it.name == value }
        } ?: true
    }
}
