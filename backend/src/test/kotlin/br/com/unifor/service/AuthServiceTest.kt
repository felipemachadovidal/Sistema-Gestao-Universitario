package br.com.unifor.service

import br.com.unifor.domain.User
import br.com.unifor.dto.LoginRequest
import br.com.unifor.repository.UserRepository
import io.quarkus.test.InjectMock
import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import jakarta.ws.rs.NotAuthorizedException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.mindrot.jbcrypt.BCrypt

@QuarkusTest
class AuthServiceTest {

    @Inject
    lateinit var authService: AuthService

    @InjectMock
    lateinit var userRepository: UserRepository

    @Test
    @DisplayName("Deve autenticar o usuário com sucesso se a senha bater com o hash BCrypt")
    fun testLoginSuccess() {
        val rawPassword = "admin123"
        val hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt())

        val mockUser = User().apply {
            username = "admin@unifor.br"
            passwordHash = hashedPassword
            role = "ADMIN"
        }

        whenever(userRepository.findByUsername("admin@unifor.br")).thenReturn(mockUser)

        val request = LoginRequest("admin@unifor.br", rawPassword)
        val response = authService.login(request)

        assertNotNull(response.token)
        assertEquals("admin@unifor.br", response.username)
        assertEquals("ADMIN", response.role)
    }

    @Test
    @DisplayName("Deve lançar NotAuthorizedException se a senha fornecida for incorreta")
    fun testLoginWrongPassword() {
        val hashedPassword = BCrypt.hashpw("admin123", BCrypt.gensalt())
        val mockUser = User().apply {
            username = "admin@unifor.br"
            passwordHash = hashedPassword
        }

        whenever(userRepository.findByUsername("admin@unifor.br")).thenReturn(mockUser)

        val request = LoginRequest("admin@unifor.br", "senha_errada_123")

        assertThrows(NotAuthorizedException::class.java) {
            authService.login(request)
        }
    }
}