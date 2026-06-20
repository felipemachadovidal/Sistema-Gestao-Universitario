package br.com.unifor.controller

import br.com.unifor.domain.User
import br.com.unifor.repository.UserRepository
import io.quarkus.test.InjectMock
import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.mindrot.jbcrypt.BCrypt

@QuarkusTest
class AuthControllerTest {

    @InjectMock
    lateinit var userRepository: UserRepository

    @Test
    @DisplayName("Deve retornar 200 OK e o token JWT ao logar com as credenciais válidas mapeadas pelo mock")
    fun testEndpointLoginSuccess() {
        val rawPassword = "admin123"
        // Gera o hash real que o AuthService espera decodificar
        val hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt())

        val mockUser = User().apply {
            username = "admin@unifor.br"
            passwordHash = hashedPassword
            role = "ADMIN"
        }

        // Intercepta a busca do banco de dados no ambiente de teste
        whenever(userRepository.findByUsername("admin@unifor.br")).thenReturn(mockUser)

        val credentials = mapOf(
            "username" to "admin@unifor.br",
            "javaPassword" to rawPassword
        )

        given()
            .contentType(ContentType.JSON)
            .body(credentials)
            .`when`()
            .post("/api/auth/login")
            .then()
            .statusCode(200) // Agora sim vai retornar 200 OK
            .body("token", notNullValue())
            .body("username", `is`("admin@unifor.br"))
            .body("role", `is`("ADMIN"))
    }

    @Test
    @DisplayName("Deve retornar 401 Unauthorized ao errar as credenciais no endpoint HTTP")
    fun testEndpointLoginInvalidCredentials() {
        val hashedPassword = BCrypt.hashpw("admin123", BCrypt.gensalt())
        val mockUser = User().apply {
            username = "admin@unifor.br"
            passwordHash = hashedPassword
        }

        whenever(userRepository.findByUsername("admin@unifor.br")).thenReturn(mockUser)

        val badCredentials = mapOf(
            "username" to "admin@unifor.br",
            "javaPassword" to "senhaErradaInvalida"
        )

        given()
            .contentType(ContentType.JSON)
            .body(badCredentials)
            .`when`()
            .post("/api/auth/login")
            .then()
            .statusCode(401)
    }
}