package routing

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import services.task.TaskService


fun Route.taskRouting() {
    val tasksService by inject<TaskService>()

    route("/tasks") {
        post {
            call.respond(tasksService.addTask(call.receive()))
        }

        post("/update") {
            tasksService.updateTask(call.receive())

            call.respond(HttpStatusCode.OK)
        }

        delete {
            val taskId = call.parameters.saveReceive("taskId") { it.toIntOrNull() }
            tasksService.deleteTask(taskId)

            call.respond(HttpStatusCode.OK)
        }
    }
}
