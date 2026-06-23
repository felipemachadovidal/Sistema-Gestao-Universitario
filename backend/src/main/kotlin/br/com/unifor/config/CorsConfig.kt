package br.com.unifor.config

import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.event.Observes
import io.vertx.ext.web.Router

@ApplicationScoped
class CorsConfig {

    fun init(@Observes router: Router) {
        router.route().order(-1).handler { context ->
            val response = context.response()

            response.putHeader("Access-Control-Allow-Origin", "http://localhost:4200")
            response.putHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE, OPTIONS")
            response.putHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With")
            response.putHeader("Access-Control-Allow-Credentials", "true")

            if (context.request().method().name() == "OPTIONS") {
                response.setStatusCode(200).end()
            } else {
                context.next()
            }
        }
    }
}