package profiler.routing

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.coroutines.channels.consumeEach
import org.koin.ktor.ext.inject
import profiler.services.InvocationService

fun Route.statisticsRouting() {
    val invocationService by inject<InvocationService>()

    route("/statistics") {
        get {
            val statistics = invocationService.getStatistics()

            call.respond(statistics)
        }

        webSocket("/subscribe") {
            val (id, channel) = invocationService.subscribeOnUpdates()
            try {
                channel.consumeEach {
                    sendSerialized(it)
                }
            } finally {
                invocationService.unsubscribeFromUpdates(id)
            }
        }
    }
}
