package dev.wigger.mood.controller

import dev.wigger.mood.dto.*
import dev.wigger.mood.entry.Entry
import dev.wigger.mood.entry.EntryService
import dev.wigger.mood.user.UserService
import dev.wigger.mood.util.mapper.WebApplicationMapperException
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
@SecurityScheme(
    scheme = "bearer",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
)
class EntryController {
    @Inject
    private lateinit var entryService: EntryService

    @Inject
    private lateinit var usersService: UserService
    
    @GET @Path("/entry")
    @RolesAllowed("USER")
    fun get(ctx: SecurityContext): List<EntryDto>? =
        entryService.findByUserId(UUID.fromString(ctx.userPrincipal.name)).toDtoList()
    
    @GET @Path("/entry/{id}")
    @RolesAllowed("USER")
    fun getById(id: Long, ctx: SecurityContext): EntryDto =
        entryService.findByIdAndUserId(id, UUID.fromString(ctx.userPrincipal.name)).toDto()

    @DELETE @Path("/entry/{id}")
    @RolesAllowed("USER")
    @Transactional
    fun delete(@PathParam("id") id: Long, ctx: SecurityContext) {
        val entry = entryService.findEntityByIdAndUserId(id, UUID.fromString(ctx.userPrincipal.name))
        
        entryService.deleteById(entry.id)
    }
    
    @PUT @Path("/entry/{id}")
    @RolesAllowed("USER")
    @Transactional
    fun update(
        @PathParam("id") id: Long,
        @Valid payload: EntryUpdateDto,
        ctx: SecurityContext,
    ) {
        val entry = entryService.findEntityByIdAndUserId(id, UUID.fromString(ctx.userPrincipal.name))
        
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
    @RolesAllowed("USER")
    @Transactional
    fun persist(@Valid payload: List<EntrySaveDto>, ctx: SecurityContext) {
        val users = usersService.findByIdUUID(UUID.fromString(ctx.userPrincipal.name))

        payload.groupingBy { it.date }
            .eachCount()
            .filter { it.value > 1 }
            .values
            .let {
                if (it.isNotEmpty()) {
                    throw WebApplicationMapperException("Duplicate date entries are not allowed", 422)
                }
            }
        
        entryService.findByUserIdAndDateException(UUID.fromString(ctx.userPrincipal.name), payload.map { it.date })
        
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
