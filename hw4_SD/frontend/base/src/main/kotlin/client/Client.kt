package client

import dom.Element
import kotlinx.browser.document
import react.Props
import react.ReactElement
import react.dom.client.createRoot


fun withReact(appBuilder: () -> ReactElement<Props>) {
    val container = document.createElement("div")
    document.body!!.appendChild(container)

    val app = appBuilder()
    createRoot(container.unsafeCast<Element>()).render(app)
}