package br.com.unifor.service

import br.com.unifor.domain.Student
import br.com.unifor.dto.StudentRequest
import br.com.unifor.repository.StudentRepository
import io.quarkus.test.InjectMock
import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import jakarta.ws.rs.WebApplicationException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`

@QuarkusTest
class StudentServiceTest {

    @Inject
    lateinit var studentService: StudentService

    @InjectMock
    lateinit var studentRepository: StudentRepository

    @Test
    @DisplayName("Deve lançar WebApplicationException 409 quando o CPF já estiver cadastrado")
    fun testCreateStudentDuplicateCpf() {
        val duplicateCpf = "11122233344"
        val request = StudentRequest("Felipe Vidal", "felipe@unifor.br", duplicateCpf)

        val existingStudent = Student().apply { cpf = duplicateCpf }
        `when`(studentRepository.findByCpf(duplicateCpf)).thenReturn(existingStudent)

        val exception = assertThrows(WebApplicationException::class.java) {
            studentService.create(request)
        }

        assertEquals(409, exception.response.status)
    }
}