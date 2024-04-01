package dev.wigger.mood.mail

import com.mailgun.api.v3.MailgunMessagesApi
import com.mailgun.client.MailgunClient
import com.mailgun.model.message.Message
import com.mailgun.model.message.MessageResponse
import feign.Logger
import feign.Request
import feign.Retryer
import io.sentry.Sentry
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.ws.rs.Produces
import org.eclipse.microprofile.config.inject.ConfigProperty
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

@ApplicationScoped
class Mailgun {
    @Inject @ConfigProperty(name = "mailgun.domain")
    private lateinit var mailgunDomain: String

    @Inject @ConfigProperty(name = "mailgun.from")
    private lateinit var mailgunFrom: String

    @Inject @ConfigProperty(name = "mailgun.api.key")
    private lateinit var mailgunApi: String

    @Inject @ConfigProperty(name = "mailgun.base_url")
    private lateinit var mailgunBaseUrl: String

    @Produces
    fun mailgunMessagesApi(): MailgunMessagesApi = MailgunClient.config(mailgunBaseUrl, mailgunApi)
        .logLevel(Logger.Level.BASIC)
        .retryer(Retryer.Default())
        .options(Request.Options(10, TimeUnit.SECONDS, 60, TimeUnit.SECONDS, true))
        .createAsyncApi(MailgunMessagesApi::class.java)

    fun sendMessage(message: Message): CompletableFuture<MessageResponse> = try {
        mailgunMessagesApi().sendMessageAsync(mailgunDomain, message)
    } catch (e: Exception) {
        Sentry.captureException(e)
        CompletableFuture<MessageResponse>()
    }
    
    fun buildMessage(
        to: String,
        subject: String,
    ): Message = Message.builder()
        .from("Mood <$mailgunFrom>")
        .to(to)
        .subject(subject)
        .html("test")
        .build()
}
