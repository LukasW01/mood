package dev.wigger.mood.resource

import dev.wigger.mood.dto.EntryDto
import dev.wigger.mood.dto.EntrySaveDto
import dev.wigger.mood.dto.EntryUpdateDto
import dev.wigger.mood.model.Entry
import dev.wigger.mood.service.EntryService
import dev.wigger.mood.service.UserService
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.ws.rs.*
import io.quarkus.logging.Log
import io.quarkus.security.Authenticated
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme

@Path("/api/v1") @ApplicationScoped @Produces("application/json") @SecurityScheme(scheme = "bearer", type = SecuritySchemeType.HTTP, bearerFormat = "JWT")
class EntryResource {
    @Inject
    private lateinit var entryService: EntryService
    @Inject
    private lateinit var usersService: UserService

    @GET @Path("/entry") @Authenticated
    fun get(): List<EntryDto> {
        val entries = entryService.findByUserId(4) ?: throw NotFoundException("No Entry found")
        
        return entries.map { entry ->
            EntryDto(
                mood = entry.mood,
                journal = entry.journal ?: "",
                date = entry.date,
                color = entry.color,
            )
        }
    }

    @GET @Path("/entry/{id}") @Authenticated
    fun getByID(id: Long): EntryDto {
        val entries = entryService.findByIdAndUserId(id, 4) ?: throw WebApplicationException("No Entry found", 404)
        
        return EntryDto(
            mood = entries.mood,
            journal = entries.journal ?: "",
            date = entries.date,
            color = entries.color,
        )
    }
    
    @POST @Path("/entry") @Transactional @Consumes("application/json") @Authenticated
    fun save(@Valid payload: List<EntrySaveDto>) {
        val users = usersService.findByIdOrNull(4) ?: throw WebApplicationException("No User found", 404)
            
            
        Log.info("Saving payload: '${payload}'")
        payload.forEach {
            entryService.persistOne(Entry().apply {
                mood = it.mood
                journal = it.journal
                date = it.date
                color = it.color
                user = users 
        })}
    }
    
    @DELETE @Path("/entry/{id}") @Transactional @Authenticated
    fun delete(@PathParam("id") id: Long) {
        val user = usersService.findByIdOrNull(4) ?: throw WebApplicationException("No User found", 400)
        entryService.findByIdAndUserId(id, user.id) ?: throw WebApplicationException("No Entry found", 400)
        
        Log.info("Deleting entry with id: '${id}'")
        entryService.deleteById(id)
    }
    
    @PUT @Path("/entry/{id}") @Transactional @Consumes("application/json") @Authenticated
    fun update(@PathParam("id") id: Long, @Valid payload: EntryUpdateDto) {
        val users = usersService.findByIdOrNull(4) ?: throw WebApplicationException("No User found", 400)
        val entries = entryService.findByIdAndUserId(id, users.id) ?: throw WebApplicationException("No Entry found", 404)
        
        Log.info("Updating entry with id: '${id}'")
        entryService.updateOne(
            id,
            Entry().apply {
                mood = payload.mood ?: entries.mood
                journal = payload.journal ?: entries.journal
                date = payload.date ?: entries.date
                color = payload.color ?: entries.color
                user = entries.user
            }
        )
    }
}