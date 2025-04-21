package dev.wigger.mood.templates.extension

import io.quarkus.qute.TemplateExtension
import java.time.LocalDateTime

@TemplateExtension(namespace = "datetime")
object DateTime {
    @JvmStatic
    fun now(): LocalDateTime = LocalDateTime.now()
}
