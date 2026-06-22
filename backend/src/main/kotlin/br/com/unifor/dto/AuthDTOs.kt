package br.com.unifor.dto

import jakarta.validation.constraints.NotBlank

data class LoginRequest(
    @field:NotBlank(message = "O usuário/e-mail é obrigatório")
    val username: String,

    @field:NotBlank(message = "A senha é obrigatória")
    val password: String
)


data class LoginResponse(
    val token: String,
    val username: String,
    val role: String
)