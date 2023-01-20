import common.profiler.ProfilerMethodStatistics
import csstype.FontWeight
import csstype.px
import emotion.react.css
import emotion.styled.styled
import react.FC
import react.Props
import react.dom.html.ReactHTML.h2
import react.dom.html.ReactHTML.p
import react.dom.html.ReactHTML.span

external interface StatisticsProps : Props {
    var methods: List<ProfilerMethodStatistics>
}

val sp = p.styled { _, _ ->
    margin = 0.px
}

val Statistics = FC<StatisticsProps> { props ->
    val methods = props.methods.map { it.plain() }

    h2 {
        +"Method invocation statistics"
    }

    methods.groupBy { it.packageName }.forEach { (packageName, classes) ->
        sp {
            +"--- $packageName".preserveSpaces()
        }
        classes.groupBy { it.className }.mapValues { (className, methods) ->
            sp {
                +"    |--- $className".preserveSpaces()
            }
            methods.forEach {
                sp {
                    +"    |    |--- ".preserveSpaces()
                    span {
                        css {
                            fontWeight = FontWeight.bold
                        }

                        +it.methodName
                    }
                }
                sp {
                    +"    |    |    |--- COUNT: ${it.count}".preserveSpaces()
                }
                sp {
                    +"    |    |    |--- SUM: ${it.sum}".preserveSpaces()
                }
                sp {
                    +"    |    |    |--- AVG: ${it.average}".preserveSpaces()
                }
            }
        }
    }
}

data class PlainMethodStatistics(
    val packageName: String,
    val className: String,
    val methodName: String,
    val count: Long,
    val sum: Double,
    val average: Double
)

fun ProfilerMethodStatistics.plain() = PlainMethodStatistics(
    packageName = methodInfo.packageName,
    className = methodInfo.declaringClassName,
    methodName = methodInfo.name,
    count = numberOfInvocations,
    sum = summaryExecutionTimeInMillis,
    average = averageExecutionTimeInMillis
)

fun String.preserveSpaces() = replace(" ", "\u00A0")
