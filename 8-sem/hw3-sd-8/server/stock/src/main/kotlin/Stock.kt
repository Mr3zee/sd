import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.routing.*
import modules.stockModules
import routing.apiRouting

fun main(args: Array<String>) = EngineMain.main(args)

@Suppress("unused")
fun Application.module() = baseServer(stockModules) {
    routing {
        apiRouting()
    }
}
