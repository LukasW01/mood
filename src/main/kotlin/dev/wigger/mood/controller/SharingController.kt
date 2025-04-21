package dev.wigger.mood.controller

import dev.wigger.mood.dto.SharingDelegatorDto
import dev.wigger.mood.dto.SharingSubmitDto
import dev.wigger.mood.dto.SharingUpdateDto
import dev.wigger.mood.dto.UuidTokenDto
import dev.wigger.mood.dto.toDelegatorDto
import dev.wigger.mood.entry.EntryService
import dev.wigger.mood.shareing.Sharing
import dev.wigger.mood.shareing.SharingService
import dev.wigger.mood.user.UserService
import dev.wigger.mood.util.mapper.WebApplicationMapperException
import dev.wigger.mood.util.userUuid
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
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.SecurityContext
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme
import java.util.UUID

@ApplicationScoped
@Path("/")
@Produces(MediaType.APPLICATION_XML)
@Consumes(MediaType.APPLICATION_JSON)
@SecurityScheme(
    scheme = "bearer",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
)
class SharingController {
    @Inject
    private lateinit var sharingService: SharingService
    
    @Inject
    private lateinit var userService: UserService
    
    @Inject
    private lateinit var entryService: EntryService

    @GET
    @Path("/sharing/delegator")
    @RolesAllowed("USER")
    fun delegator(ctx: SecurityContext): List<SharingDelegatorDto> =
        sharingService.findByUserUuid(ctx.userUuid()).map { sharing ->
            sharing.apply {
                entry = entryService.findByIdPermission(sharing.delegator.id, sharing.permissions)
            }.toDelegatorDto()
        }

    @DELETE
    @Path("/sharing/{id}")
    @RolesAllowed("USER")
    @Transactional
    fun delete(id: UUID, ctx: SecurityContext) =
        sharingService.findByUserAndDelegator(ctx.userUuid(), id).let { sharing ->
            sharingService.deleteByUserUuid(ctx.userUuid(), sharing.delegator.id)
        }

    @PUT
    @Path("/sharing/{id}")
    @RolesAllowed("USER")
    @Transactional
    fun update(
        @Valid payload: SharingUpdateDto,
        id: UUID,
        ctx: SecurityContext,
    ) =
        sharingService.findByUserAndDelegator(ctx.userUuid(), id)
            .apply { permissions = payload.permissions }
            .let { sharing -> sharingService.updateOne(sharing) }

    @PUT
    @Path("/sharing/token/create")
    @RolesAllowed("USER")
    @Transactional
    fun createToken(ctx: SecurityContext): UuidTokenDto =
        UUID.randomUUID().let { token ->
            userService.findByIdUuid(ctx.userUuid()).let { user ->
                userService.updateOne(user.apply { sharingToken = token })
            }
            UuidTokenDto(token)
        }

    @POST
    @Path("/sharing/token/connect/{token}")
    @RolesAllowed("USER")
    @Transactional
    fun connectToken(
        @Valid payload: SharingSubmitDto,
        token: UUID,
        ctx: SecurityContext
    ) {
        Pair(userService.findByIdUuid(ctx.userUuid()), userService.findBySharingToken(token)).let { (user, delegator) ->
            sharingService.findByUserAndDelegatorExists(user.id, delegator.id)

            if (user.id == delegator.id) {
                throw WebApplicationMapperException("Cannot connect the same user", 422)
            }

            sharingService.persistOne(Sharing().apply {
                this.user = user
                this.delegator = delegator
                this.permissions = payload.permissions
            })
        }
    }
}
