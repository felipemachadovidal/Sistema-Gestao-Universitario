package br.com.unifor.controller

import br.com.unifor.dto.LoginRequest
import br.com.unifor.dto.LoginResponse
import br.com.unifor.service.AuthService
import jakarta.validation.Valid
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class AuthController(private val authService: AuthService) {

    @POST
    @Path("/login")
    fun login(@Valid request: LoginRequest): Response {
        val response = authService.login(request)
        return Response.ok(response).build()
    }
}