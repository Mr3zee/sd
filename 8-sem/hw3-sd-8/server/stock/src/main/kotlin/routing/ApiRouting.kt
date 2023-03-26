package routing

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import service.AdminStockService

fun Routing.apiRouting() {
    val service by inject<AdminStockService>()

    route("/api") {
        get("/info") {
            val info = service.getStockInfo(call.code) ?: error("unknown code parameter")
            call.respond(info)
        }

        get("/all") {
            val all = service.getAllStocks()
            call.respond(all)
        }

        delete("/sell") {
            val result = service.sellStocks(call.code, call.quantity)
            call.respond(result)
        }

        put("/buy") {
            val result = service.buyStocks(call.code, call.quantity)
            call.respond(result)
        }
    }

    route("/admin") {
        adminAccess()

        route("/update") {
            put("/value") {
                service.updateCompany(call.code, call.value)
                call.respondOk()
            }

            put("/quantity") {
                service.updateCompany(call.code, stockQuantity = call.quantity)
                call.respondOk()
            }

            delete("/clear") {
                service.clear()
                call.respondOk()
            }
        }

        route("/add") {
            put("/stokes") {
                service.addStokes(call.code, call.quantity)
                call.respondOk()
            }

            put("/company") {
                service.addCompany(call.name, call.code, call.value, call.quantity)
                call.respondOk()
            }
        }
    }
}

private suspend fun ApplicationCall.respondOk() = respond(HttpStatusCode.OK)

private val ApplicationCall.name get() = parameters.saveReceive("name")
private val ApplicationCall.code get() = parameters.saveReceive("code")
private val ApplicationCall.value get() = parameters.saveReceive("value") { it.toDoubleOrNull() }
private val ApplicationCall.quantity get() = parameters.saveReceive("quantity") { it.toIntOrNull() }

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
