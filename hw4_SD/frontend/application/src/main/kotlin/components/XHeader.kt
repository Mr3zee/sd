package components

import csstype.*
import mui.material.*
import mui.material.styles.TypographyVariant
import mui.system.sx
import react.FC
import react.Props
import react.dom.html.ReactHTML.div


external interface XHeaderProps : Props {
    var loading: Boolean
}

val XHeader = FC<XHeaderProps> { props ->
    AppBar {
        sx {
            display = Display.flex
            flexDirection = FlexDirection.row
            justifyContent = JustifyContent.left
            alignItems = AlignItems.center

            height = 56.px
        }

        position = AppBarPosition.static

        Typography {
            variant = TypographyVariant.h4
            component = div
            sx {
                marginRight = 12.px
                marginLeft = 12.px
            }

            +"My Planners"
        }

        if (props.loading) {
            Box {
                CircularProgress {
                    color = CircularProgressColor.secondary
                }
            }
        }
    }
}