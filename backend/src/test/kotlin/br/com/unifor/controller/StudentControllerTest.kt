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
            .contentType(ContentType.JSON)
            .body(validStudent)
            .`when`()
            .post("/api/students")
            .then()
            .statusCode(201)
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
            .statusCode(400)
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

    @Test
    @DisplayName("Deve retornar 400 Bad Request se o nome do aluno tiver menos de 3 caracteres")
    fun testCreateStudentNameTooShort() {
        val invalidStudent = mapOf(
            "name" to "Fe",
            "email" to "felipe@unifor.br",
            "cpf" to "12345678901"
        )

        given()
            .contentType(ContentType.JSON)
            .body(invalidStudent)
            .`when`()
            .post("/api/students")
            .then()
            .statusCode(400)
    }

    @Test
    @DisplayName("Deve retornar 204 No Content ao realizar o Soft Delete de um estudante")
    fun testDeleteStudentSuccess() {
        val studentId = given()
            .contentType(ContentType.JSON)
            .body(mapOf("name" to "Estudante Deletar", "email" to "del@u.com", "cpf" to "99988877766"))
            .post("/api/students")
            .then().statusCode(201).extract().path<Int>("id")

        given()
            .`when`()
            .delete("/api/students/$studentId")
            .then()
            .statusCode(204)
    }

    @Test
    @DisplayName("Deve retornar 200 OK e a lista de estudantes ativos ao acessar o endpoint GET")
    fun testGetAllStudentsSuccess() {
        given()
            .`when`()
            .get("/api/students")
            .then()
            .statusCode(200)
            .body("size()", notNullValue())
    }
}