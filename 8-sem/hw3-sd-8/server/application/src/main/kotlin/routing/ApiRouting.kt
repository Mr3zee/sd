package routing

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import service.UserService

fun Routing.apiRouting() {
    val service by inject<UserService>()

    route("/api") {
        get("/login") {
            val exists = service.login(call.id)
            val status = if (exists) HttpStatusCode.OK else HttpStatusCode.NotFound
            call.respond(status)
        }

        post("/register") {
            val id = service.register()
            call.respond(id)
        }

        put("/deposit") {
            service.deposit(call.id, call.amountDouble)
            call.respondOk()
        }

        get("/portfolio") {
            call.respond(service.getPortfolio(call.id))
        }

        get("/account-info") {
            val info = service.getAccountInfo(call.id) ?: error("unknown id")
            call.respond(info)
        }

        get("/stocks") {
            call.respond(service.getAvailableStocks())
        }

        put("/buy") {
            call.respond(service.buyStock(call.id, call.code, call.amountInt))
        }

        delete("/sell") {
            call.respond(service.sellStock(call.id, call.code, call.amountInt))
        }
    }
}

private suspend fun ApplicationCall.respondOk() = respond(HttpStatusCode.OK)

private val ApplicationCall.id get() = parameters.saveReceive("id") { it.toIntOrNull() }
private val ApplicationCall.amountInt get() = parameters.saveReceive("amount") { it.toIntOrNull() }
private val ApplicationCall.amountDouble get() = parameters.saveReceive("amount") { it.toDoubleOrNull() }
private val ApplicationCall.code get() = parameters.saveReceive("code")
