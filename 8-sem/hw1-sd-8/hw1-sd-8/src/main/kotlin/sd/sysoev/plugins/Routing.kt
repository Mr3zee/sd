package sd.sysoev.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import sd.sysoev.actor.SearchRequest
import sd.sysoev.actor.createChildActor
import sd.sysoev.actor.createMasterActor
import sd.sysoev.api.BingApi
import sd.sysoev.api.GoogleApi
import sd.sysoev.api.YandexApi

fun Application.routing(timeoutMillis: Long) {
    val googleApi by inject<GoogleApi>()
    val bingApi by inject<BingApi>()
    val yandexApi by inject<YandexApi>()

    routing {
        get("/") {
            val query = call.parameters["query"]
            if (query == null) {
                call.respondText("No query parameter")
                return@get
            }

            val google = createChildActor("google", googleApi)
            val yandex = createChildActor("yandex", yandexApi)
            val bing = createChildActor("bing", bingApi)

            val results = createMasterActor(listOf(google, yandex, bing)).use {
                it.sendChannel.send(SearchRequest(query))
                it.awaitAllAndClose(timeoutMillis)
            }
            println("result: ${results.size}")

            call.respond(results)
        }
    }
}
