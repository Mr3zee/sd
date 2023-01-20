import common.api.Planner
import components.XDashboard
import components.XHeader
import react.FC
import react.Props
import react.useEffect
import react.useState
import utils.sendAsyncApiRequest


val App = FC<Props> {
    var planners by useState<List<Planner>>()

    useEffect {
        if (planners == null) {
            sendAsyncApiRequest<List<Planner>>(
                path = "planners",
            ) {
                planners = it
            }
        }
    }

    XHeader {
        loading = planners == null
    }

    planners?.let {
        XDashboard {
            this.planners = it
        }
    }
}
