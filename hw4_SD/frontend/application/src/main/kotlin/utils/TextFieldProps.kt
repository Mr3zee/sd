package utils

import dom.html.HTMLElement
import mui.material.InputProps
import react.dom.events.ChangeEvent
import react.dom.events.ChangeEventHandler

fun ChangeEvent<HTMLElement>.value(): String = target.asDynamic().value as String

fun InputProps.onChange(handler: ChangeEventHandler<HTMLElement>) {
    asDynamic().onChange = handler
}
