package dev.wigger.mood

import dev.wigger.mood.controller.InfoController
import dev.wigger.mood.dto.InfoResponse
import io.quarkus.test.junit.QuarkusTest

import io.vertx.core.http.HttpServerRequest
import io.vertx.core.net.SocketAddress
import io.vertx.ext.web.RoutingContext
import jakarta.ws.rs.core.Response
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@QuarkusTest
class InfoControllerTest {
    val infoController = InfoController()

    @Mock
    lateinit var routingContext: RoutingContext

    @Mock
    lateinit var httpServerRequest: HttpServerRequest

    @Mock
    lateinit var socketAddress: SocketAddress

    @BeforeEach
    fun init() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `test ip`() {
        // Set the mocked RoutingContext to return the predefined IP address
        `when`(routingContext.request()).thenReturn(httpServerRequest)
        `when`(httpServerRequest.remoteAddress()).thenReturn(socketAddress)
        `when`(socketAddress.host()).thenReturn("192.168.1.1")

        val response = infoController.ip(routingContext)
        assertEquals("192.168.1.1", response.ip)
    }

    @Test
    fun `test health check`() {
        val response = infoController.health()
        assertEquals(Response.Status.OK.statusCode, response.status)
        assertEquals(InfoResponse("OK", Response.Status.OK.statusCode), response)
    }
}
