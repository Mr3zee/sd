@file:Suppress("SuspiciousCollectionReassignment")

package components

import common.api.Task
import csstype.Display
import csstype.FlexDirection
import csstype.FontSize
import emotion.react.css
import mui.material.Checkbox
import mui.material.Input
import mui.system.sx
import react.FC
import react.Fragment
import react.dom.html.ReactHTML.div
import react.useState
import utils.StyledProps
import utils.sendAsyncApiPostRequest
import utils.value


external interface XTasksListProps : StyledProps {
    var plannerId: Int
    var tasks: List<Task>
}

val XTasksList = FC<XTasksListProps> { props ->
    var tasks by useState(props.tasks)

    div {
        css {
            display = Display.flex
            flexDirection = FlexDirection.column
        }

        tasks.forEach { task ->
            Fragment {
                key = task.id.toString()

                XTask {
                    this.task = task
                }
            }
        }

        div {
            css {
                display = Display.flex
                flexDirection = FlexDirection.row
            }

            Checkbox {
                checked = true
                disabled = true
            }

            Input {
                fullWidth = true
                disabled = true
                disableUnderline = true

                sx {
                    fontSize = FontSize.small
                }

                placeholder = "Click to add new task"

                onClick = {
                    val task = Task(name = "", plannerId = props.plannerId)

                    sendAsyncApiPostRequest(
                        path = "tasks",
                        body = task
                    ) { id: Int ->
                        tasks += task.copy(id = id)
                    }
                }
            }
        }
    }
}
