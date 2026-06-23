package br.com.unifor.service

import br.com.unifor.dto.LoginRequest
import br.com.unifor.dto.LoginResponse
import br.com.unifor.repository.UserRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.NotAuthorizedException
import org.jboss.logging.Logger
import org.mindrot.jbcrypt.BCrypt
import io.smallrye.jwt.build.Jwt
import org.eclipse.microprofile.config.inject.ConfigProperty
import java.time.Duration

@ApplicationScoped
class AuthService(private val userRepository: UserRepository) {

    private val log: Logger = Logger.getLogger(AuthService::class.java)

    @ConfigProperty(name = "smallrye.jwt.sign.key.string")
    lateinit var jwtSecret: String

    fun login(request: LoginRequest): LoginResponse {
        log.info("Tentativa de login para o usuário: ${request.username}")

        val user = userRepository.findByUsername(request.username)
            ?: throw NotAuthorizedException("Usuário ou senha inválidos.")

        val cleanPassword = request.password
            .replace(Regex("[\\p{Cc}\\h]"), "")
            .trim()

        val isLoginSuccessful = BCrypt.checkpw(cleanPassword, user.passwordHash)

        if (!isLoginSuccessful) {
            log.warn("Senha inválida para o usuário: ${request.username}")
            throw NotAuthorizedException("Usuário ou senha inválidos.")
        }

        log.info("Usuário ${user.username} autenticado com sucesso. Gerando JWT...")

        val tokenRealJwt = Jwt.issuer("unifor-auth-api")
            .upn(user.username)
            .groups(setOf(user.role))
            .expiresIn(Duration.ofHours(2))
            .signWithSecret(jwtSecret)

        return LoginResponse(
            token = tokenRealJwt,
            username = user.username!!,
            role = user.role
        )
    }
}