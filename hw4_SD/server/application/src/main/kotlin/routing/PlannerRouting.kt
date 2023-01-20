package routing

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import services.planner.PlannerService


fun Route.plannerRouting() {
    val plannerService by inject<PlannerService>()

    route("/planners") {
        get {
            call.respond(plannerService.presentPlanners())
        }

        get("/archived") {
            call.respond(plannerService.archivedPlanners())
        }

        post {
            call.respond(plannerService.addPlanner(call.receive()))
        }

        post("/update") {
            plannerService.updatePlanner(call.receive())

            call.respond(HttpStatusCode.OK)
        }

        post("/unarchive") {
            val plannerId = call.parameters.saveReceive("plannerId") { it.toIntOrNull() }
            plannerService.archivePlanner(plannerId, value = false)

            call.respond(HttpStatusCode.OK)
        }

        post("/archive") {
            val plannerId = call.parameters.saveReceive("plannerId") { it.toIntOrNull() }
            plannerService.archivePlanner(plannerId, value = true)

            call.respond(HttpStatusCode.OK)
        }
    }
}
