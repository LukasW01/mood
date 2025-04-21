package dev.wigger.mood.mail

import io.quarkus.logging.Log
import io.quarkus.mailer.Mail
import io.quarkus.mailer.reactive.ReactiveMailer
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject

@ApplicationScoped
class Mailer {
    @Inject
    private lateinit var mailer: ReactiveMailer

    /**
     * A mailer to send email asynchronously
     * 
     * @param to
     * @param subject 
     * @param body <html>
     */
    fun send(
        to: String,
        subject: String,
        body: String,
    ): Uni<Void> = try {
        mailer.send(Mail.withHtml(to, subject, body))
    } catch (e: Exception) {
        Log.error(e.message)
        Uni.createFrom().failure(e)
    }
}
