package br.com.unifor.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class CourseRequest(
    @field:NotBlank(message = "O nome do curso é obrigatório")
    @field:Size(min = 5, max = 150, message = "O nome do curso deve ter entre 5 e 150 caracteres")
    val name: String,

    @field:Size(max = 500, message = "A descrição não pode passar de 500 caracteres")
    val description: String?,

    @field:NotNull(message = "A carga horária é obrigatória")
    @field:Min(value = 1, message = "A carga horária deve ser de no mínimo 1 hora")
    val durationHours: Int
)


data class CourseResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val durationHours: Int
)