package br.com.unifor.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDate

data class StudentRequest(
    @field:NotBlank(message = "O nome é obrigatório")
    @field:Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
    val name: String,

    @field:NotBlank(message = "O e-mail é obrigatório")
    @field:Email(message = "O e-mail deve ser válido")
    val email: String,

    @field:NotBlank(message = "O CPF é obrigatório")
    @field:Size(min = 11, max = 14, message = "CPF inválido")
    val cpf: String
)


data class StudentResponse(
    val id: Long,
    val name: String,
    val email: String,
    val cpf: String,
    val registrationDate: LocalDate
)