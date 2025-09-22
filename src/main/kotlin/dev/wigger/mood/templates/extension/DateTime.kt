package dev.wigger.mood.templates.extension

import io.quarkus.qute.TemplateExtension
import java.time.LocalDateTime

@TemplateExtension(namespace = "datetime")
object DateTime {
    @JvmStatic
    fun now(): LocalDateTime = LocalDateTime.now()
    
    @JvmStatic
    fun minus(days: Long): LocalDateTime = LocalDateTime.now().minusDays(days)
    
    @JvmStatic
    fun plus(days: Long): LocalDateTime = LocalDateTime.now().plusDays(days)
}
