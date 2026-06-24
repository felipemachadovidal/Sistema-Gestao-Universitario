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

    @Test
    @DisplayName("Deve matricular um aluno com sucesso e retornar 200 OK")
    fun testEnrollStudentSuccess() {
        val studentId = given()
            .contentType(ContentType.JSON)
            .body(mapOf("name" to "Felipe Teste", "email" to "felipe.test@u.com", "cpf" to "11122233344"))
            .post("/api/students")
            .then().statusCode(201).extract().path<Int>("id").toLong()

        val courseId = given()
            .contentType(ContentType.JSON)
            .body(mapOf("name" to "Kotlin com Quarkus", "description" to "Design de APIs", "durationHours" to 60))
            .post("/api/courses")
            .then().statusCode(201).extract().path<Int>("id").toLong()

        given()
            .contentType(ContentType.JSON)
            .pathParam("courseId", courseId)
            .pathParam("studentId", studentId)
            .body("{}")
            .`when`()
            .post("/api/courses/{courseId}/enroll/{studentId}")
            .then()
            .statusCode(200)
            .body("message", `is`("Aluno matriculado com sucesso!"))
    }

    @Test
    @DisplayName("Deve retornar 200 OK ao buscar a lista de alunos matriculados em um curso")
    fun testGetEnrolledStudentsSuccess() {
        val courseId = given()
            .contentType(ContentType.JSON)
            .body(mapOf("name" to "Curso Geral", "description" to "Desc", "durationHours" to 30))
            .post("/api/courses")
            .then().statusCode(201).extract().path<Int>("id").toLong()

        given()
            .pathParam("courseId", courseId)
            .`when`()
            .get("/api/courses/{courseId}/students")
            .then()
            .statusCode(200)
            .body("size()", notNullValue())
    }

    @Test
    @DisplayName("Deve remover a matrícula de um aluno com sucesso e retornar 204 No Content")
    fun testUnenrollStudentSuccess() {
        val studentId = given()
            .contentType(ContentType.JSON)
            .body(mapOf("name" to "Dev Junior", "email" to "dev@u.com", "cpf" to "22233344455"))
            .post("/api/students")
            .then().statusCode(201).extract().path<Int>("id").toLong()

        val courseId = given()
            .contentType(ContentType.JSON)
            .body(mapOf("name" to "Design de Sistemas", "description" to "Desc", "durationHours" to 20))
            .post("/api/courses")
            .then().statusCode(201).extract().path<Int>("id").toLong()

        given()
            .contentType(ContentType.JSON)
            .pathParam("courseId", courseId)
            .pathParam("studentId", studentId)
            .body("{}")
            .post("/api/courses/{courseId}/enroll/{studentId}")
            .then().statusCode(200)

        given()
            .pathParam("courseId", courseId)
            .pathParam("studentId", studentId)
            .`when`()
            .delete("/api/courses/{courseId}/unenroll/{studentId}")
            .then()
            .statusCode(204)
    }
}