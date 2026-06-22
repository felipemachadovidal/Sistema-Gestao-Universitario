package br.com.unifor.service

import br.com.unifor.domain.Student
import br.com.unifor.dto.StudentRequest
import br.com.unifor.repository.StudentRepository
import io.quarkus.test.InjectMock
import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import jakarta.ws.rs.WebApplicationException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.mockito.kotlin.doAnswer
import org.jboss.logging.Logger

@QuarkusTest
class StudentServiceTest {

    @Inject
    lateinit var studentService: StudentService

    @InjectMock
    lateinit var studentRepository: StudentRepository

    private val log: Logger = Logger.getLogger(StudentServiceTest::class.java)

    @Test
    @DisplayName("Deve cadastrar um estudante com sucesso quando os dados do DTO forem válidos")
    fun testCreateStudentSuccess() {
        val request = StudentRequest("Felipe Vidal", "felipe@unifor.br", "12345678901")

        whenever(studentRepository.findByCpf("12345678901")).thenReturn(null)
        whenever(studentRepository.findByEmail("felipe@unifor.br")).thenReturn(null)

        doAnswer { invocation ->
            val student = invocation.arguments[0] as Student
            student.id = 1L
            null
        }.whenever(studentRepository).persist(any<Student>())

        val response = studentService.create(request)

        assertNotNull(response.id)
        assertEquals("Felipe Vidal", response.name)
    }

    @Test
    @DisplayName("Deve lançar WebApplicationException 409 quando o CPF já estiver cadastrado")
    fun testCreateStudentDuplicateCpf() {
        val duplicateCpf = "11122233344"
        val request = StudentRequest("Felipe Vidal", "felipe@unifor.br", duplicateCpf)

        val existingStudent = Student().apply { cpf = duplicateCpf }
        Mockito.`when`(studentRepository.findByCpf(duplicateCpf)).thenReturn(existingStudent)

        val exception = assertThrows(WebApplicationException::class.java) {
            studentService.create(request)
        }

        assertEquals(409, exception.response.status)
    }

    @Test
    @DisplayName("Deve lançar BadRequest ao tentar atualizar um aluno que sofreu soft delete")
    fun testUpdateStudentSoftDeleted() {
        val studentId = 1L
        val request = StudentRequest("Felipe Vidal", "felipe@unifor.br", "12345678901")

        val deletedStudent = Student().apply {
            id = studentId
            name = "Antigo Nome"
            deleted = true
        }

        Mockito.`when`(studentRepository.findById(studentId)).thenReturn(deletedStudent)

        val exception = assertThrows(WebApplicationException::class.java) {
            studentService.update(studentId, request)
        }

        assertEquals(400, exception.response.status)
    }

    @Test
    @DisplayName("Deve lançar BadRequest se o CPF contiver caracteres inválidos ou tamanho incorreto")
    fun testCreateStudentInvalidCpfFormat() {
        val request = StudentRequest("Felipe Vidal", "felipe@unifor.br", "123.ABC.789-10")

        val exception = assertThrows(WebApplicationException::class.java) {
            studentService.create(request)
        }

        assertEquals(400, exception.response.status)
    }
}