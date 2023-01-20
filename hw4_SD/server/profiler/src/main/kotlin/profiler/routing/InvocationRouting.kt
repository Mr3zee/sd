package profiler.routing

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import profiler.services.InvocationService

fun Route.invocationRouting() {
    val invocationService by inject<InvocationService>()

    post("invocation") {
        invocationService.registerInvocation(call.receive())

        call.respond(HttpStatusCode.OK)
    }
}
