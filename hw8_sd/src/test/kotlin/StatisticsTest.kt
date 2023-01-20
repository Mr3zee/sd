import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.junit5.KoinTestExtension
import org.koin.test.mock.declare
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalUnit
import kotlin.test.assertEquals


class StatisticsTest : KoinTest {
    @JvmField
    @RegisterExtension
    @Suppress("unused")
    val koinTestExtension = KoinTestExtension.create {
        modules(
            module {
                single<IClock> { Clock() }
            }
        )
    }

    @Test
    fun `test empty`() = withTimeContext {
        assertEquals(emptyMap(), provider.getAllEventStatistic())
    }

    @Test
    fun `test too old stats`() = withTimeContext {
        incWithOffset("test", 61)

        assertEquals(mapOf("test" to EventStatistics("test", 0.0)), provider.getAllEventStatistic())
    }

    @Test
    fun `test multiple stats`() = withTimeContext {
        incWithOffset("test1", 32)
        incWithOffset("test1", 41)
        incWithOffset("test1", 93)
        incWithOffset("test1", 1)
        incWithOffset("test1", 54)

        incWithOffset("test2", 54)
        incWithOffset("test2", 100)

        val expectedTest1_1 = EventStatistics("test1", 4 / 60.0)
        val expectedTest2_1 = EventStatistics("test2", 1 / 60.0)

        assertEquals(expectedTest1_1, provider.getEventStatisticByName("test1"))
        assertEquals(expectedTest2_1, provider.getEventStatisticByName("test2"))

        assertEquals(
            expected = mapOf("test1" to expectedTest1_1, "test2" to expectedTest2_1),
            actual = provider.getAllEventStatistic()
        )

        clock.setNow(pivot.plus(7, ChronoUnit.MINUTES))

        val expectedTest1_2 = EventStatistics("test1", 3 / 60.0)
        val expectedTest2_2 = EventStatistics("test2", 0.0)

        assertEquals(expectedTest1_2, provider.getEventStatisticByName("test1"))
        assertEquals(expectedTest2_2, provider.getEventStatisticByName("test2"))

        assertEquals(
            expected = mapOf("test1" to expectedTest1_2, "test2" to expectedTest2_2),
            actual = provider.getAllEventStatistic()
        )

        assertEquals(
            expected = """
                test1 -> ${3 / 60.0}
                test2 -> ${0.0}
            """.trimIndent(),
            actual = provider.printStatistic()
        )
    }

    private fun withTimeContext(body: TimeContext.() -> Unit) {
        val pivot = Instant.ofEpochMilli(100_000_000)
        val clock = TestClock(pivot)

        declare<IClock> { clock }

        val context = TimeContext(pivot, EventStatisticProviderImpl(), clock)

        body(context)
    }

    private data class TimeContext(
        val pivot: Instant,
        val provider: EventStatisticProvider,
        val clock: TestClock
    ) {
        fun incWithOffset(name: String, offset: Long, units: TemporalUnit = ChronoUnit.MINUTES) {
            clock.setNow(pivot.minus(offset, units))
            provider.incEvent(name)
            clock.setNow(pivot)
        }
    }
}
