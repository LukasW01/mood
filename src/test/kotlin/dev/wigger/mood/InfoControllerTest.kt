package dev.wigger.mood

import dev.wigger.mood.controller.InfoController
import dev.wigger.mood.dto.InfoResponse
import io.quarkus.test.junit.QuarkusTest

import io.vertx.core.http.HttpServerRequest
import io.vertx.core.net.SocketAddress
import io.vertx.ext.web.RoutingContext
import jakarta.ws.rs.core.Response
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@QuarkusTest
class InfoControllerTest {
    private val infoController = InfoController()

    @Test
    fun testIp() {
        // Mock the RoutingContext and its request() and remoteAddress() methods
        val mockContext = mock(RoutingContext::class.java)
        val mockRequest = mock(HttpServerRequest::class.java)
        val mockRemoteAddress = mock(SocketAddress::class.java)

        // Set the mocked RoutingContext to return the predefined IP address
        `when`(mockContext.request()).thenReturn(mockRequest)
        `when`(mockRequest.remoteAddress()).thenReturn(mockRemoteAddress)
        `when`(mockRemoteAddress.host()).thenReturn("192.168.1.1")

        val response = infoController.ip(mockContext)
        assertEquals("192.168.1.1", response.ip)
    }

    @Test
    fun testHealth() {
        val response = infoController.health()
        assertEquals(Response.Status.OK.statusCode, response.status)
        assertEquals(InfoResponse("OK", Response.Status.OK.statusCode), response)
    }
}
