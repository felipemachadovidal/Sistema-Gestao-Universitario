package br.com.unifor.controller

import br.com.unifor.dto.LoginRequest
import br.com.unifor.dto.LoginResponse
import br.com.unifor.service.AuthService
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class AuthController(private val authService: AuthService) {

    @POST
    @Path("/login")
    fun login(request: LoginRequest): Response {
        return try {
            val response = authService.login(request)
            Response.ok(response).build()
        } catch (e: Exception) {
            Response.status(Response.Status.UNAUTHORIZED)
                .entity(mapOf("error" to e.message))
                .build()
        }
    }
}