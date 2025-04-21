package dev.wigger.mood.util.annotation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Constraint(validatedBy = [MoodValidator::class])
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Mood(
    val message: String = "Invalid mood",
    val groups: Array<KClass<*>> = [],
    @Suppress("TYPE_ALIAS")
    val payload: Array<KClass<out Payload>> = [],
)
