package server

import io.ktor.server.config.*

interface ApplicationPropertyProvider {
    fun propertyOrNull(path: String): ApplicationConfigValue?

    fun property(path: String): ApplicationConfigValue
}
