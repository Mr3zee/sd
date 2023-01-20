@file:Suppress("SuspiciousCollectionReassignment")

package components

import common.api.Planner
import csstype.*
import emotion.react.css
import emotion.styled.styled
import mui.icons.material.Add
import mui.material.*
import react.FC
import react.Fragment
import react.Props
import react.dom.aria.ariaLabel
import react.dom.html.ReactHTML.div
import react.useState
import utils.sendAsyncApiPostRequest


external interface XPlannerContainerProps : Props {
    var planners: List<Planner>
}

val plannerDiv = div.styled { _, _ ->
    width = 32.pct
}

val XDashboard = FC<XPlannerContainerProps> { props ->
    var planners by useState(props.planners)

    div {
        css {
            display = Display.flex
            flexDirection = FlexDirection.row
            flexWrap = FlexWrap.wrap
            justifyContent = JustifyContent.flexStart

            rowGap = 16.px
            columnGap = 2.pct

            paddingTop = 16.px
        }

        planners.forEach { planner ->
            Fragment {
                key = planner.id.toString()

                plannerDiv {
                    XPlanner {
                        this.planner = planner
                        this.onDelete = { deleted ->
                            sendAsyncApiPostRequest<Unit>(
                                path = "planners/archive",
                                parameters = {
                                    put("plannerId", deleted.id)
                                }
                            ) {
                                planners = planners.filter { it.id != deleted.id }
                            }
                        }
                    }
                }
            }
        }
    }

    div {
        css {
            position = Position.fixed

            right = 24.px
            bottom = 24.px
        }

        Fab {
            color = FabColor.primary
            ariaLabel = "Add planner"

            Add()

            onClick = {
                val planner = Planner(
                    name = "Untitled",
                )

                sendAsyncApiPostRequest(
                    path = "planners",
                    body = planner
                ) { id: Int ->
                    planners += planner.copy(id = id)
                }
            }
        }
    }
}
