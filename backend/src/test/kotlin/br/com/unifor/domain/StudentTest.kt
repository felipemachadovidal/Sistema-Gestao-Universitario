package br.com.unifor.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDate

class StudentTest {

    @Test
    @DisplayName("Deve inicializar a entidade Student com as regras de negócio e valores padrões corretos")
    fun testStudentDefaultValues() {
        val student = Student().apply {
            name = "Felipe Vidal"
            email = "felipe@unifor.br"
            cpf = "12345678901"
        }

        assertFalse(student.deleted, "O aluno deve iniciar com a flag deleted como false (Soft Delete ativo)")
        assertNotNull(student.registrationDate, "A data de registro deve ser gerada automaticamente")
        assertEquals(LocalDate.now(), student.registrationDate, "A data de registro deve ser a data de hoje")
    }
}