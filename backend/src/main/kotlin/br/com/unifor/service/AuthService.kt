package br.com.unifor.service

import br.com.unifor.dto.LoginRequest
import br.com.unifor.dto.LoginResponse
import br.com.unifor.repository.UserRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.NotAuthorizedException
import org.jboss.logging.Logger
import org.mindrot.jbcrypt.BCrypt
import java.util.UUID

@ApplicationScoped
class AuthService(private val userRepository: UserRepository) {

    private val log: Logger = Logger.getLogger(AuthService::class.java)

    fun login(request: LoginRequest): LoginResponse {
        log.info("Tentativa de login para o usuário: ${request.username}")

        val user = userRepository.findByUsername(request.username)
            ?: throw NotAuthorizedException("Usuário ou senha inválidos.")

        // 🌟 HIGIENIZAÇÃO COMPLETA: Remove caracteres invisíveis, nulos ou de controle de bytes
        val senhaLimpa = request.password
            .replace(Regex("[\\p{Cc}\\h]"), "") // Remove caracteres de controle e espaços horizontais ocultos
            .trim()

        // Faz o teste com a senha totalmente higienizada
        val loginSucesso = BCrypt.checkpw(senhaLimpa, user.passwordHash?.trim())

        log.info("👉 Senha Higienizada: [$senhaLimpa]")
        log.info("👉 Resultado do Match Definitivo: $loginSucesso")

        val hashGeradoNaHora = BCrypt.hashpw("admin123", BCrypt.gensalt())
        log.info("🚨 COPIE ESTE HASH DO CONSOLE: $hashGeradoNaHora")

        if (!loginSucesso) {
            log.warn("Senha inválida para o usuário: ${request.username}")
            throw NotAuthorizedException("Usuário ou senha inválidos.")
        }

        log.info("Usuário ${user.username} autenticado com sucesso.")
        return LoginResponse(
            token = UUID.randomUUID().toString(),
            username = user.username!!,
            role = user.role
        )
    }
}