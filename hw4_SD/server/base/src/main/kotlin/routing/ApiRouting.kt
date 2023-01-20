package routing

import io.ktor.server.routing.*

fun Routing.apiRouting(build: Route.() -> Unit) {
    route("/api") {
        build()
    }
}
