package dev.wigger.mood.util.annotation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class HexColorValidator : ConstraintValidator<HexColor, String> {
    override fun isValid(value: String, context: ConstraintValidatorContext?): Boolean =
        "^#(?:[0-9a-fA-F]{3}){1,2}$".toRegex().matches(value)
}
