import common.profiler.ProfilerStatistics
import csstype.Display
import csstype.FlexDirection
import emotion.react.css
import io.ktor.client.plugins.websocket.*
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.useEffect
import react.useState
import utils.HttpUtils
import utils.Ui
import utils.launch

val App = FC<Props> {
    var profilerStatistics by useState<ProfilerStatistics>()
    var firstTime by useState(true)

    useEffect {
        if (!firstTime) return@useEffect

        firstTime = false

        launch(Ui) {
            HttpUtils.client.webSocket("/api/statistics/subscribe") {
                while (true) {
                    profilerStatistics = receiveDeserialized()
                }
            }
        }
    }

    profilerStatistics?.methodStatistics?.let {
        div {
            css {
                display = Display.flex
                flexDirection = FlexDirection.column
            }

            if (it.isNotEmpty()) {
                Statistics {
                    methods = it
                }
            } else {
                +"Statistic is empty"
            }
        }
    } ?: run {
        +"Loading statistics..."
    }
}
