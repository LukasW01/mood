package dev.wigger.mood.controller

import dev.wigger.mood.dto.*
import dev.wigger.mood.shareing.Sharing
import dev.wigger.mood.shareing.SharingService
import dev.wigger.mood.user.UserService
import dev.wigger.mood.util.mapper.WebApplicationMapperException
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
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
class SharingController {
    @Inject
    private lateinit var sharingService: SharingService
    
    @Inject
    private lateinit var userService: UserService
    
    @GET @Path("/sharing/delegator")
    @RolesAllowed("USER")
    fun getDelegator(ctx: SecurityContext): List<SharingDelegatorDto> {
        val user = userService.findByMail(ctx.userPrincipal.name)
        
        return sharingService.findByUserId(user.id).map { sharing -> sharingService.mapToDto(sharing) }
    }

    @GET @Path("/sharing/token/create")
    @RolesAllowed("USER")
    @Transactional
    fun createToken(ctx: SecurityContext): SharingTokenDto {
        val user = userService.findByMail(ctx.userPrincipal.name)
        val token = UUID.randomUUID()
        
        userService.updateOne(
            user.id,
            user.apply { sharingToken = token },
        )
        
        return SharingTokenDto(token)
    }
    
    @POST @Path("/sharing/token/connect")
    @RolesAllowed("USER")
    @Transactional
    fun connectToken(@Valid payload: SharingTokenDto, ctx: SecurityContext) {
        val users = userService.findByMail(ctx.userPrincipal.name)
        val delegators = userService.findBySharingToken(payload.token)

        if (users.id == delegators.id) {
            throw WebApplicationMapperException("Cannot connect the same user", 422)
        }
        
        sharingService.findByUserIdAndDelegatorIdException(users.id, delegators.id)

        sharingService.persistOne(Sharing().apply {
            user = users
            delegator = delegators
        })
    }
}
