package br.com.unifor.controller // Use o seu pacote real

import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.event.Observes
import io.vertx.ext.web.Router

@ApplicationScoped
class CorsConfig {

    // Este método intercepta o servidor na camada mais baixa (Vert.x)
    // antes mesmo do filtro de segurança rodar
    fun init(@Observes router: Router) {
        router.route().order(-1).handler { context ->
            val response = context.response()

            // Adiciona as permissões que o navegador exige
            response.putHeader("Access-Control-Allow-Origin", "http://localhost:4200")
            response.putHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE, OPTIONS")
            response.putHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With")
            response.putHeader("Access-Control-Allow-Credentials", "true")

            // Se for a checagem do navegador (OPTIONS), responde com 200 OK imediatamente e encerra
            if (context.request().method().name() == "OPTIONS") {
                response.setStatusCode(200).end()
            } else {
                context.next() // Se for o POST real, deixa seguir para o seu AuthController
            }
        }
    }
}