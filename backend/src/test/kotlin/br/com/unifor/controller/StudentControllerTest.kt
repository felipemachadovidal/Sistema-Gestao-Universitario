package br.com.unifor.controller

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@QuarkusTest
class StudentControllerTest {

    @Test
    @DisplayName("Deve cadastrar um estudante com sucesso quando os dados do DTO forem válidos")
    fun testCreateStudentSuccess() {
        val validStudent = mapOf(
            "name" to "Felipe Vidal",
            "email" to "felipe@unifor.br",
            "cpf" to "12345678901"
        )

        given()
            .contentType(ContentType.JSON) // Corrigido: adicionado o ponto inicial aqui
            .body(validStudent)
            .`when`()
            .post("/api/students")
            .then()
            .statusCode(201) // 201 Created
            .body("id", notNullValue())
            .body("name", `is`("Felipe Vidal"))
            .body("email", `is`("felipe@unifor.br"))
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando o e-mail no DTO for inválido")
    fun testCreateStudentInvalidEmail() {
        val invalidStudent = mapOf(
            "name" to "Felipe Vidal",
            "email" to "email-invalido-sem-arroba",
            "cpf" to "12345678901"
        )

        given()
            .contentType(ContentType.JSON)
            .body(invalidStudent)
            .`when`()
            .post("/api/students")
            .then()
            .statusCode(400) // Bad Request disparado pelo Hibernate Validator!
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando o CPF for em branco")
    fun testCreateStudentMissingCpf() {
        val invalidStudent = mapOf(
            "name" to "Felipe Vidal",
            "email" to "felipe@unifor.br",
            "cpf" to ""
        )

        given()
            .contentType(ContentType.JSON)
            .body(invalidStudent)
            .`when`()
            .post("/api/students")
            .then()
            .statusCode(400)
    }
}