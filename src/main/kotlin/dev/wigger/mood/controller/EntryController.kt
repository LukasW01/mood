package dev.wigger.mood.controller

import dev.wigger.mood.dto.EntryDto
import dev.wigger.mood.dto.EntrySubmitDto
import dev.wigger.mood.dto.EntryUpdateDto
import dev.wigger.mood.dto.toDto
import dev.wigger.mood.dto.toDtoList
import dev.wigger.mood.entry.Entry
import dev.wigger.mood.entry.EntryService
import dev.wigger.mood.user.UserService
import dev.wigger.mood.util.mapper.WebApplicationMapperException
import dev.wigger.mood.util.mapper.userUuid
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.DELETE
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.SecurityContext
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme

@ApplicationScoped
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
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
    
    @GET
    @Path("/entry")
    @RolesAllowed("USER")
    fun get(ctx: SecurityContext): List<EntryDto>? =
        entryService.findByUserId(ctx.userUuid()).toDtoList()
    
    @GET
    @Path("/entry/{id}")
    @RolesAllowed("USER")
    fun getById(id: Long, ctx: SecurityContext): EntryDto =
        entryService.findByIdAndUserId(id, ctx.userUuid()).toDto()

    @DELETE
    @Path("/entry/{id}")
    @RolesAllowed("USER")
    @Transactional
    fun delete(@PathParam("id") id: Long, ctx: SecurityContext) =
        entryService.deleteById(entryService.findByIdAndUserId(id, ctx.userUuid()).id)

    @PUT
    @Path("/entry/{id}")
    @RolesAllowed("USER")
    @Transactional
    fun update(
        id: Long,
        @Valid payload: EntryUpdateDto,
        ctx: SecurityContext,
    ) {
        entryService.findByIdAndUserId(id, ctx.userUuid()).apply {
            mood = payload.mood ?: mood
            journal = payload.journal ?: journal
            date = payload.date ?: date
            color = payload.color ?: color
        }.also { entry ->
            entryService.updateOne(entry)
        }
    }
    
    @POST
    @Path("/entry")
    @RolesAllowed("USER")
    @Transactional
    fun persist(payload: List<@Valid EntrySubmitDto>, ctx: SecurityContext) {
        entryService.findByUuidAndDateExists(ctx.userUuid(), payload.map { it.date })

        payload.groupingBy { it.date }
            .eachCount()
            .filter { it.value > 1 }
            .values
            .takeIf { it.isNotEmpty() }
            ?: throw WebApplicationMapperException("Duplicate date entries are not allowed", 422)

        payload.forEach {
            entryService.persistOne(Entry().apply {
                mood = it.mood
                journal = it.journal
                date = it.date
                color = it.color
                user = usersService.findByIdUuid(ctx.userUuid())
            })
        }
    }
}
