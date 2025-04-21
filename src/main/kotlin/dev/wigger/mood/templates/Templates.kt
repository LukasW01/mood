package dev.wigger.mood.templates

import dev.wigger.mood.user.Users

import io.quarkus.qute.CheckedTemplate
import io.quarkus.qute.TemplateInstance
import io.vertx.ext.web.RoutingContext

import java.time.LocalDateTime

/**
 * HTML templates and the Types they require
 */
@CheckedTemplate
object Templates {
    @JvmStatic
    external fun login(
        user: Users,
    ): TemplateInstance

    @JvmStatic
    external fun register(
        user: Users,
        context: RoutingContext,
    ): TemplateInstance

    @JvmStatic
    external fun verify(
        user: Users,
    ): TemplateInstance

    @JvmStatic
    external fun reset(
        user: Users,
        context: RoutingContext,
    ): TemplateInstance

    @JvmStatic
    external fun resetForm(
        user: Users,
    ): TemplateInstance

    @JvmStatic
    external fun success(): TemplateInstance
}
