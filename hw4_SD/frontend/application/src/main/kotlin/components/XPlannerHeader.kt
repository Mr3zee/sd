package components

import common.api.Planner
import csstype.Color
import csstype.FontSize
import mui.icons.material.Delete
import mui.material.*
import mui.material.styles.Theme
import mui.material.styles.useTheme
import mui.system.sx
import react.*
import utils.onChange
import utils.sendAsyncApiPostRequest
import utils.value


external interface XPlannerHeaderProps : Props {
    var planner: Planner
    var onDelete: (Planner) -> Unit
}

val XPlannerHeader = FC<XPlannerHeaderProps> { props ->
    var planner by useState(props.planner)
    var firstUpdate by useState(true)

    useMemo(planner) {
        when {
            firstUpdate -> firstUpdate = false
            else -> sendAsyncApiPostRequest<Planner>("planners/update", planner)
        }
    }

    val isValidName = useMemo(planner) {
        planner.name.length in 1..64
    }

    val isValidDescription = useMemo(planner) {
        planner.description?.let { it.length <= 512 } ?: true
    }

    val theme = useTheme<Theme>()

    CardHeader {
        avatar = Avatar.create {
            sx {
                backgroundColor = Color("#2a9696")
            }

            +(planner.name.firstOrNull()?.titlecase() ?: "U")
        }
        title = Input.create {
            fullWidth = true

            value = planner.name
            error = !isValidName
            onChange {
                val input = it.value()
                if (isValidName) {
                    planner = planner.copy(name = input)
                }
            }
        }
        subheader = Input.create {
            disableUnderline = true
            fullWidth = true
            multiline = true

            sx {
                fontSize = FontSize.small
                color = theme.palette.text.secondary
            }

            placeholder = "Add description"
            error = !isValidDescription
            value = planner.description ?: ""
            onChange {
                val input = it.value()
                if (isValidDescription) {
                    planner = planner.copy(description = input)
                }
            }
        }
        action = IconButton.create {
            Delete {
                fontSize = SvgIconSize.medium
            }
            onClick = {
                props.onDelete(planner)
            }
        }
    }
}
