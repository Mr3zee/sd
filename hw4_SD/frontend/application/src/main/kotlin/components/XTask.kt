package components

import common.api.Task
import csstype.*
import emotion.react.css
import mui.material.Checkbox
import mui.material.Input
import mui.system.sx
import react.FC
import react.dom.html.ReactHTML.div
import react.useMemo
import react.useState
import utils.StyledProps
import utils.onChange
import utils.sendAsyncApiPostRequest
import utils.value


external interface XTaskProps : StyledProps {
    var task : Task
}

val XTask = FC<XTaskProps> { props ->
    var task by useState(props.task)
    var firstUpdate by useState(true)

    useMemo(task) {
        when {
            firstUpdate -> firstUpdate = false
            else -> sendAsyncApiPostRequest<Task>("tasks/update", task)
        }
    }

    div {
        css {
            minHeight = 40.px

            display = Display.flex
            flexDirection = FlexDirection.row
            alignItems = AlignItems.center
        }

        Checkbox {
            checked = task.done
            onChange = { _, checked ->
                task = task.copy(done = checked)
            }
        }

        Input {
            disableUnderline = true
            fullWidth = true
            multiline = true

            sx {
                fontSize = FontSize.small
            }

            placeholder = "Add task description"
            value = task.name
            onChange {
                val input = it.value()
                task = task.copy(name = input)
            }
        }
    }
}
