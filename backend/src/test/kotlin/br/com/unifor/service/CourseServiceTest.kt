package br.com.unifor.service

import br.com.unifor.domain.Course
import br.com.unifor.domain.Student
import br.com.unifor.repository.CourseRepository
import br.com.unifor.repository.StudentRepository
import io.quarkus.test.InjectMock
import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import jakarta.ws.rs.NotFoundException
import jakarta.ws.rs.WebApplicationException
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import br.com.unifor.dto.CourseRequest
import org.junit.jupiter.api.Assertions.assertEquals

@QuarkusTest
class CourseServiceTest {

    @Inject
    lateinit var courseService: CourseService

    @InjectMock
    lateinit var courseRepository: CourseRepository

    @InjectMock
    lateinit var studentRepository: StudentRepository

    @Test
    @DisplayName("Deve estourar NotFoundException ao tentar matricular em um curso inexistente")
    fun testEnrollStudentCourseNotFound() {
        `when`(courseRepository.findActiveById(999L)).thenReturn(null)

        assertThrows(NotFoundException::class.java) {
            courseService.enrollStudent(999L, 1L)
        }
    }

    @Test
    @DisplayName("Deve estourar WebApplicationException (Conflict) ao tentar matricular aluno já matriculado")
    fun testEnrollStudentDuplicate() {
        val courseId = 1L
        val studentId = 2L

        val mockStudent = Student().apply { id = studentId; name = "Felipe"; email = "felipe@unifor.br"; cpf = "123" }
        val mockCourse = Course().apply { id = courseId; name = "Angular 17"; durationHours = 40 }

        mockCourse.students.add(mockStudent)

        `when`(courseRepository.findActiveById(courseId)).thenReturn(mockCourse)
        `when`(studentRepository.findActiveById(studentId)).thenReturn(mockStudent)

        assertThrows(WebApplicationException::class.java) {
            courseService.enrollStudent(courseId, studentId)
        }
    }

    @Test
    @DisplayName("Deve lançar BadRequest se a carga horária do curso ultrapassar 500 horas")
    fun testCreateCourseDurationExceeded() {
        val request = CourseRequest("Treinamento Avançado Quarkus", "Descrição longa", 600)

        val exception = assertThrows(WebApplicationException::class.java) {
            courseService.create(request)
        }

        assertEquals(400, exception.response.status)
    }

    @Test
    @DisplayName("Deve matricular um aluno com sucesso quando os dados existirem e não houver duplicidade")
    fun testEnrollStudentSuccess() {
        val courseId = 1L
        val studentId = 2L

        val mockStudent = Student().apply { id = studentId; name = "Felipe"; email = "felipe@unifor.br"; deleted = false }
        val mockCourse = Course().apply { id = courseId; name = "Angular 17"; durationHours = 40; deleted = false }

        `when`(courseRepository.findActiveById(courseId)).thenReturn(mockCourse)
        `when`(studentRepository.findActiveById(studentId)).thenReturn(mockStudent)

        courseService.enrollStudent(courseId, studentId)

        org.junit.jupiter.api.Assertions.assertTrue(mockCourse.students.contains(mockStudent))
    }

    @Test
    @DisplayName("Deve remover a matrícula de um aluno com sucesso")
    fun testUnenrollStudentSuccess() {
        val courseId = 1L
        val studentId = 2L

        val mockStudent = Student().apply { id = studentId; name = "Felipe"; deleted = false }
        val mockCourse = Course().apply { id = courseId; name = "Angular 17"; deleted = false }

        mockCourse.students.add(mockStudent)

        `when`(courseRepository.findActiveById(courseId)).thenReturn(mockCourse)
        `when`(studentRepository.findActiveById(studentId)).thenReturn(mockStudent)

        courseService.unenrollStudent(courseId, studentId)
        org.junit.jupiter.api.Assertions.assertFalse(mockCourse.students.contains(mockStudent))
    }

    @Test
    @DisplayName("Deve lançar WebApplicationException (Bad Request) ao tentar desvincular aluno que não está no curso")
    fun testUnenrollStudentNotEnrolled() {
        val courseId = 1L
        val studentId = 2L
        val mockStudent = Student().apply { id = studentId; name = "Felipe"; deleted = false }
        val mockCourse = Course().apply { id = courseId; name = "Angular 17"; deleted = false }

        `when`(courseRepository.findActiveById(courseId)).thenReturn(mockCourse)
        `when`(studentRepository.findActiveById(studentId)).thenReturn(mockStudent)

        val exception = assertThrows(WebApplicationException::class.java) {
            courseService.unenrollStudent(courseId, studentId)
        }

        assertEquals(400, exception.response.status)
    }
}