package dev.wigger.mood.controller

import dev.wigger.mood.dto.EntryDto
import dev.wigger.mood.dto.EntrySaveDto
import dev.wigger.mood.dto.EntryUpdateDto
import dev.wigger.mood.entry.Entry
import dev.wigger.mood.entry.EntryService
import dev.wigger.mood.user.UserService
import io.quarkus.logging.Log
import io.quarkus.security.Authenticated
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import jakarta.ws.rs.*
import jakarta.ws.rs.core.SecurityContext
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme
import java.util.UUID

@Path("/api/v1") @ApplicationScoped @Produces("application/json") @Consumes("application/json") @SecurityScheme(
    scheme = "bearer",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
)
class EntryController {
    @Inject
    private lateinit var entryService: EntryService

    @Inject
    private lateinit var usersService: UserService
    
    @GET @Path("/entry") @Authenticated
    fun get(ctx: SecurityContext): List<EntryDto>? = usersService.findByUsername(ctx.userPrincipal.name)
        ?.let { entryService.findByUserId(it.id) }
        ?: throw WebApplicationException("No Entry found", 400)
    
    @GET @Path("/entry/{id}") @Authenticated
    fun getById(id: UUID, ctx: SecurityContext): EntryDto = usersService.findByUsername(ctx.userPrincipal.name)
        ?.let { entryService.findByIdAndUserId(id, it.id) }
        ?: throw WebApplicationException("No Entry found", 400)

    @DELETE @Path("/entry/{id}") @Transactional @Authenticated
    fun delete(@PathParam("id") id: UUID, ctx: SecurityContext) {
        val entry = usersService.findByUsername(ctx.userPrincipal.name)
            ?.let { entryService.findEntityByIdAndUserId(id, it.id) }
            ?: throw WebApplicationException("No Entry found", 400)
        
        Log.info("Deleting entry with id: '${entry.id}'")
        entryService.deleteById(entry.id)
    }
    
    @PUT @Path("/entry/{id}") @Transactional @Authenticated
    fun update(@PathParam("id") id: UUID, @Valid payload: EntryUpdateDto, ctx: SecurityContext) {
        val entry = usersService.findByUsername(ctx.userPrincipal.name)
            ?.let { entryService.findEntityByIdAndUserId(id, it.id) }
            ?: throw WebApplicationException("No Entry found", 404)
        
        Log.info("Updating entry with id: '${entry.id}'")
        entryService.updateOne(
            id,
            Entry().apply {
                mood = payload.mood ?: entry.mood
                journal = payload.journal ?: entry.journal
                date = payload.date ?: entry.date
                color = payload.color ?: entry.color
                user = entry.user
            },
        )
    }
    
    @POST @Path("/entry") @Transactional @Authenticated
    fun save(@Valid payload: List<EntrySaveDto>, ctx: SecurityContext) {
        val users = usersService.findByUsername(ctx.userPrincipal.name)
            ?: throw WebApplicationException("No User found", 404)

        Log.info("Saving payload: '$payload'")
        payload.forEach {
            entryService.persistOne(Entry().apply {
                mood = it.mood
                journal = it.journal
                date = it.date
                color = it.color
                user = users
            })
        }
    }
}
