package routing

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import service.StockService

fun Routing.apiRouting() {
    val service by inject<StockService>()

    route("/api") {
        get("/info") {
            val info = service.getStockInfo(call.code) ?: error("unknown code parameter")
            call.respond(info)
        }

        post("/buy") {
            val result = service.buyStocks(call.code, call.quantity)
            call.respond(result)
        }
    }

    route("/admin") {
        adminAccess()

        route("/update") {
            put("/value") {
                service.updateCompany(call.code, call.value)
            }

            put("/quantity") {
                service.updateCompany(call.code, stockQuantity = call.quantity)
            }
        }

        route("/add") {
            put("/stokes") {
                service.addStokes(call.code, call.quantity)
            }

            put("/company") {
                service.addCompany(call.name, call.code, call.value, call.quantity)
            }
        }
    }
}

private val ApplicationCall.name get() = parameters.saveReceive("name")
private val ApplicationCall.code get() = parameters.saveReceive("code")
private val ApplicationCall.value get() = parameters.saveReceive("value") { it.toDouble() }
private val ApplicationCall.quantity get() = parameters.saveReceive("quantity") { it.toInt() }

// top security
private fun Route.adminAccess() {
    intercept(ApplicationCallPipeline.Plugins) {
        val id = call.parameters["id"]
        if (id != "admin") {
            call.respond(HttpStatusCode.Unauthorized)
            finish()
            return@intercept
        }
        proceed()
    }
}
