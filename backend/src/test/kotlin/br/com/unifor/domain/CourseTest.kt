package br.com.unifor.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class CourseTest {

    @Test
    @DisplayName("Deve inicializar a entidade Course com coleções vazias e flags de status corretas")
    fun testCourseInitialization() {
        val course = Course().apply {
            name = "Análise e Desenvolvimento de Sistemas"
            description = "Formação em arquitetura de software e backend"
            durationHours = 2000
        }

        assertFalse(course.deleted, "O curso deve iniciar ativo (flag deleted deve ser false)")
        assertNotNull(course.students, "A lista de estudantes deve ser instanciada automaticamente")
        assertEquals(0, course.students.size, "O curso deve iniciar com zero alunos matriculados")
    }
}