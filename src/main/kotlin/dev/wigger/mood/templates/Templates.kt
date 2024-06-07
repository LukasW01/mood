package dev.wigger.mood.templates

import dev.wigger.mood.dto.*
import dev.wigger.mood.user.Users
import io.quarkus.qute.CheckedTemplate
import io.quarkus.qute.TemplateInstance
import java.time.LocalDateTime

@CheckedTemplate
object Templates {
    @JvmStatic
    external fun login(
        user: Users,
        ip: String,
        year: Int = LocalDateTime.now().year,
    ): TemplateInstance

    @JvmStatic
    external fun register(
        user: RegisterDto,
        ip: String,
        link: String,
        year: Int = LocalDateTime.now().year,
    ): TemplateInstance

    @JvmStatic
    external fun verify(
        user: Users,
        ip: String,
        yesterday: LocalDateTime = LocalDateTime.now().minusDays(1),
        year: Int = LocalDateTime.now().year,
    ): TemplateInstance

    @JvmStatic
    external fun reset(
        user: Users,
        ip: String,
        link: String,
        year: Int = LocalDateTime.now().year,
    ): TemplateInstance

    @JvmStatic
    external fun resetForm(
        user: Users,
        ip: String,
        year: Int = LocalDateTime.now().year,
    ): TemplateInstance
}
