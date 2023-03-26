import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.routing.*
import modules.applicationModules
import routing.apiRouting

fun main(args: Array<String>) = EngineMain.main(args)

@Suppress("unused")
fun Application.module() = baseServer(applicationModules) {
    routing {
        apiRouting()
    }
}
