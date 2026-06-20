package br.com.unifor.controller

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@QuarkusTest
class CourseControllerTest {

    @Test
    @DisplayName("Deve criar um curso com sucesso quando os dados do DTO forem válidos")
    fun testCreateCourseSuccess() {
        val validCourse = mapOf(
            "name" to "Angular 17 Avançado",
            "description" to "Dominando standalone components, SSR e signals",
            "durationHours" to 45
        )

        given()
            .contentType(ContentType.JSON)
            .body(validCourse)
            .`when`()
            .post("/api/courses")
            .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("name", `is`("Angular 17 Avançado"))
            .body("durationHours", `is`(45))
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request se o nome do curso for enviado em branco")
    fun testCreateCourseMissingName() {
        val invalidCourse = mapOf(
            "name" to "",
            "description" to "Descrição de um curso sem nome",
            "durationHours" to 20
        )

        given()
            .contentType(ContentType.JSON)
            .body(invalidCourse)
            .`when`()
            .post("/api/courses")
            .then()
            .statusCode(400)
    }

    @Test
    @DisplayName("Deve retornar 200 OK ao buscar a lista de cursos ativos")
    fun testGetAllCoursesSuccess() {
        given()
            .`when`()
            .get("/api/courses")
            .then()
            .statusCode(200)
            .body("size()", notNullValue())
    }
}