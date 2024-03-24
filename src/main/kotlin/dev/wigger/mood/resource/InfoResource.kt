package dev.wigger.mood.resource

import dev.wigger.mood.dto.ErrorResponse
import dev.wigger.mood.dto.IP
import io.vertx.ext.web.RoutingContext
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Response

@Path("/api/v1") @ApplicationScoped @Produces("application/json")
class InfoResource {
    @Inject
    private lateinit var context: RoutingContext
    
    @GET @Path("/info/ip")
    fun ip(): IP = IP(context.request().remoteAddress().host())

    @GET @Path("/info/health")
    fun health(): Response = Response.status(Response.Status.OK).entity(
        ErrorResponse("Healthy!", Response.Status.OK.statusCode)
    ).build()
}