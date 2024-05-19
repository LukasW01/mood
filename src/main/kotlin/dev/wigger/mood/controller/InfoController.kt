package dev.wigger.mood.controller

import dev.wigger.mood.dto.ErrorResponse
import dev.wigger.mood.dto.Ip
import io.vertx.ext.web.RoutingContext
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.jetbrains.annotations.VisibleForTesting

@ApplicationScoped
@Path("/") @Produces(MediaType.APPLICATION_JSON) @Consumes(MediaType.APPLICATION_JSON)
class InfoController {
    @GET @Path("/ip")
    fun ip(context: RoutingContext): Ip = Ip(context.request().remoteAddress().host())

    @GET @Path("/health")
    fun health(): Response = Response.status(Response.Status.OK).entity(ErrorResponse("Healthy!", Response.Status.OK.statusCode)).build()
}
