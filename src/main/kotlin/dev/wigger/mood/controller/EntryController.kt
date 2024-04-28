package dev.wigger.mood.controller

import dev.wigger.mood.dto.EntryDto
import dev.wigger.mood.dto.EntrySaveDto
import dev.wigger.mood.dto.EntryUpdateDto
import dev.wigger.mood.entry.Entry
import dev.wigger.mood.entry.EntryService
import dev.wigger.mood.user.UserService
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.SecurityContext
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme
import java.util.UUID

@ApplicationScoped
@Path("/") @Produces(MediaType.APPLICATION_JSON) @Consumes(MediaType.APPLICATION_JSON)
@SecurityScheme(scheme = "bearer", type = SecuritySchemeType.HTTP, bearerFormat = "JWT")
class EntryController {
    @Inject
    private lateinit var entryService: EntryService

    @Inject
    private lateinit var usersService: UserService
    
    @GET @Path("/entry")
    @RolesAllowed("user")
    fun get(ctx: SecurityContext): List<EntryDto>? = usersService.findByUsername(ctx.userPrincipal.name)
        .let { entryService.findByUserId(it.id) }
        ?: throw WebApplicationException("No Entry found", 400)
    
    @GET @Path("/entry/{id}")
    @RolesAllowed("user")
    fun getById(id: UUID, ctx: SecurityContext): EntryDto = usersService.findByUsername(ctx.userPrincipal.name)
        .let { entryService.findByIdAndUserId(id, it.id) }
        ?: throw WebApplicationException("No Entry found", 400)

    @DELETE @Path("/entry/{id}")
    @RolesAllowed("user")
    @Transactional
    fun delete(@PathParam("id") id: UUID, ctx: SecurityContext) {
        val entry = usersService.findByUsername(ctx.userPrincipal.name)
            .let { entryService.findEntityByIdAndUserId(id, it.id) }
            ?: throw WebApplicationException("No Entry found", 400)
        
        entryService.deleteById(entry.id)
    }
    
    @PUT @Path("/entry/{id}")
    @RolesAllowed("user")
    @Transactional
    fun update(@PathParam("id") id: UUID, @Valid payload: EntryUpdateDto, ctx: SecurityContext) {
        val entry = usersService.findByUsername(ctx.userPrincipal.name)
            .let { entryService.findEntityByIdAndUserId(id, it.id) }
            ?: throw WebApplicationException("No Entry found", 404)
        
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
    
    @POST @Path("/entry")
    @RolesAllowed("user")
    @Transactional
    fun save(@Valid payload: List<EntrySaveDto>, ctx: SecurityContext) {
        val users = usersService.findByUsername(ctx.userPrincipal.name)

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
