import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.Instant

data class EventStatistics(
    val name: String, val rpm: Double
)

interface EventStatisticProvider {
    fun incEvent(name: String)
    fun getEventStatisticByName(name: String): EventStatistics?
    fun getAllEventStatistic(): Map<String, EventStatistics>
    fun printStatistic(): String
}

class EventStatisticProviderImpl : EventStatisticProvider, KoinComponent {
    private val clock by inject<IClock>()
    private var stats = mutableMapOf<String, MutableList<Instant>>()

    override fun incEvent(name: String): Unit = withSanitise {
        stats.putIfAbsent(name, mutableListOf())
        stats[name]!!.add(it)
    }

    override fun getEventStatisticByName(name: String): EventStatistics? = withSanitise {
        stats[name]?.let { EventStatistics(name, it.rpm()) }
    }

    override fun getAllEventStatistic(): Map<String, EventStatistics> = withSanitise {
        stats.mapValues { (k, v) -> EventStatistics(k, v.rpm()) }
    }

    override fun printStatistic(): String = withSanitise {
        stats.entries.joinToString("\n") { (k, v) ->
            "$k -> ${v.rpm()}"
        }
    }

    private fun List<*>.rpm() = count() / 60.0

    private fun <T> withSanitise(body: (now: Instant) -> T): T {
        val now = clock.now

        stats = stats.mapValues { (_, v) ->
            v.filter { IClock.minutesFromNow(now, it) <= 60L }.toMutableList()
        }.toMutableMap()

        return body(now)
    }
}
