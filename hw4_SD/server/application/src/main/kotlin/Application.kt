import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.routing.*
import modules.applicationModules
import routing.apiRouting
import routing.plannerRouting
import routing.taskRouting
import server.*

fun main(args: Array<String>): Unit = EngineMain.main(args)

@Suppress("unused")
fun Application.module() = basicServer(applicationModules) {
    routing {
        apiRouting {
            plannerRouting()
            taskRouting()
        }
    }
}
