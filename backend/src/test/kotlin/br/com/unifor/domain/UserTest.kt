package br.com.unifor.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class UserTest {

    @Test
    @DisplayName("Deve instanciar o modelo de usuário com os campos de autenticação e roles corretos")
    fun testUserProperties() {
        val user = User().apply {
            username = "coordenador@unifor.br"
            passwordHash = "$2a$10\$X5vV8X..."
            role = "ADMIN"
        }

        assertNotNull(user)
        assertEquals("coordenador@unifor.br", user.username)
        assertEquals("ADMIN", user.role)
    }
}