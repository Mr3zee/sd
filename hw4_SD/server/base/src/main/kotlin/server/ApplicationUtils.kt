package server

import io.ktor.server.application.*
import mu.KotlinLogging
import org.slf4j.event.Level

val Application.logger by lazy {
    KotlinLogging.logger("Application")
}

@Suppress("unused")
fun Application.isDevEnv(): Boolean {
    val env = propertyOrNull("ktor.environment")?.getString()
    return env == "development"
}

fun Application.propertyOrNull(path: String) = environment.config.propertyOrNull(path)

fun Application.property(path: String) = propertyOrNull(path) ?: error("Expected Application server.property $path")

val Application.loggerLevel: Level
    get() = propertyOrNull("ktor.logger.level")?.let {
        Level.valueOf(it.getString())
    } ?: Level.INFO

fun Level.koinLevel() = org.koin.core.logger.Level.valueOf(name)
