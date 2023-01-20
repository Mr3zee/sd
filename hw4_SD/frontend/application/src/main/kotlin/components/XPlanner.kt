package components

import common.api.Planner
import mui.material.Card
import mui.material.CardContent
import react.FC
import react.Props


external interface XPlannerProps : Props {
    var planner: Planner
    var onDelete: (Planner) -> Unit
}

val XPlanner = FC<XPlannerProps> { props ->
    Card {
        XPlannerHeader {
            planner = props.planner
            onDelete = props.onDelete
        }

        CardContent {
            XTasksList {
                tasks = props.planner.tasks
                plannerId = props.planner.id
            }
        }
    }
}
