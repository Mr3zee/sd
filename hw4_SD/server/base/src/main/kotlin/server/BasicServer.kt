package server

import db.connection.postgres
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.ContentTransformationException
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.core.module.Module
import org.koin.environmentProperties
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.slf4j.event.Level
import routing.images.images
import routing.indexRouting

fun Application.basicServer(
    applicationModules: List<Module> = emptyList(),
    build: Application.() -> Unit
) {
    install(ContentNegotiation) {
        json()
    }

    configureCORS()

    install(CallLogging) {
        level = this@basicServer.loggerLevel
        filter { call -> call.request.path().startsWith("/") }
    }

    install(Koin) {
        slf4jLogger(this@basicServer.loggerLevel.koinLevel())

        environmentProperties()

        val app = this@basicServer

        val propertiesProviderModule = org.koin.dsl.module {
            single<ApplicationPropertyProvider> {
                object : ApplicationPropertyProvider {
                    override fun propertyOrNull(path: String): ApplicationConfigValue? {
                        return app.propertyOrNull(path)
                    }

                    override fun property(path: String): ApplicationConfigValue {
                        return app.property(path)
                    }
                }
            }
        }

        modules(applicationModules + propertiesProviderModule)
    }

    install(StatusPages) {
        exception<IllegalArgumentException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, cause.message ?: "")
        }

        exception<ContentTransformationException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, cause.message ?: "")
        }

        exception<BadRequestException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, cause.cause?.message ?: "")
        }
    }

    postgres(isDebug = loggerLevel != Level.INFO)

    routing {
        indexRouting()
        images()
    }

    build()
}
