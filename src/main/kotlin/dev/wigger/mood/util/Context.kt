package dev.wigger.mood.util

import io.vertx.core.net.HostAndPort
import io.vertx.ext.web.RoutingContext
import jakarta.ws.rs.core.SecurityContext
import java.util.UUID

fun SecurityContext.userUuid(): UUID = UUID.fromString(userPrincipal.name)

fun RoutingContext.ip(): String = request().remoteAddress().host()

fun RoutingContext.host(): HostAndPort = request().authority()
