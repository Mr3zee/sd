package profiler

import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.json.Json
import profiler.modules.applicationModules
import profiler.routing.invocationRouting
import profiler.routing.statisticsRouting
import routing.apiRouting
import server.basicServer

fun main(args: Array<String>) = EngineMain.main(args)

@Suppress("unused")
fun Application.module() = basicServer(applicationModules) {
    install(WebSockets) {
        contentConverter = KotlinxWebsocketSerializationConverter(Json)
    }

    routing {
        apiRouting {
            invocationRouting()
            statisticsRouting()
        }
    }
}
