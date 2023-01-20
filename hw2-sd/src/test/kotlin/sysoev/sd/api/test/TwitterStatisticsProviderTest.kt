package sysoev.sd.api.test

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.koin.core.component.inject
import org.koin.test.mock.declare
import sysoev.sd.api.client.TwitterGranularity
import sysoev.sd.api.client.TwitterHttpClient
import sysoev.sd.api.client.TwitterResponse
import sysoev.sd.api.client.TwitterResponseData
import sysoev.sd.api.statistics.TwitterStatisticsInvalidRangeException
import sysoev.sd.api.statistics.TwitterStatisticsProvider
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit


class TwitterStatisticsProviderTest : BaseTwitterTest() {
    private val provider by inject<TwitterStatisticsProvider>()

    @Test
    fun `test empty result for hashtag`() = withTestClient {
        val response = provider.requestStatisticsByHours("empty", 10)
        assertEquals(emptyList<Int>(), response.byHours)
    }

    @Test
    fun `test non empty responses`() = withTestClient {
        val response = provider.requestStatisticsByHours("hashtag", 4)

        val expected = listOf(3, 4, 5, 13)
        assertEquals(expected, response.byHours)

        val response2 = provider.requestStatisticsByHours("hashtag", 6)

        val expected2 = listOf(3, 4, 5, 13, 82, 0)
        assertEquals(expected2, response2.byHours)

        val response3 = provider.requestStatisticsByHours("hashtag", 1)

        val expected3 = listOf(3)
        assertEquals(expected3, response3.byHours)
    }

    @Test
    fun `test not valid range`() = withTestClient {
        org.junit.jupiter.api.assertThrows<TwitterStatisticsInvalidRangeException> {
            provider.requestStatisticsByHours("hashtag", 0)
        }

        org.junit.jupiter.api.assertThrows<TwitterStatisticsInvalidRangeException> {
            provider.requestStatisticsByHours("hashtag", -1)
        }

        org.junit.jupiter.api.assertThrows<TwitterStatisticsInvalidRangeException> {
            provider.requestStatisticsByHours("hashtag", 25)
        }
    }

    private fun withTestClient(handler: suspend () -> Unit) = runBlocking {
        declare<TwitterHttpClient> { TestTwitterHttpClient() }
        declare { TwitterStatisticsProvider() }
        handler()
    }

    private class TestTwitterHttpClient : TwitterHttpClient {
        override suspend fun sendRequestToTwitterCountApi(
            hashtag: String,
            start: OffsetDateTime,
            end: OffsetDateTime,
            granularity: TwitterGranularity
        ): TwitterResponse {
            assertTrue(start <= end, "Start should be less or equal to end")
            assertEquals(TwitterGranularity.HOUR, granularity, "Wrong granularity parameter")

            val diff = ChronoUnit.NANOS.between(zeroHour, end)
            val absStart = start.toInstant().minus(diff, ChronoUnit.NANOS)

            assertEquals(ChronoUnit.HOURS.between(start, end), ChronoUnit.HOURS.between(absStart, zeroHour)) {
                "Invalid cast to absolute time units, time gap is is not the same"
            }

            return TwitterResponse(
                data = data[hashtag]?.mapNotNull { (time, count) ->
                    when (time.toInstant()) {
                        in absStart..zeroHour -> TwitterResponseData(time, time, count)
                        else -> null
                    }
                } ?: fail("Unknown hashtag: $hashtag, known are: ${data.keys.joinToString(", ")}")
            )
        }
    }

    companion object {
        private val zeroHour = Instant.parse("2001-08-23T00:00:00.000Z")
        private val shiftedZeroHour = zeroHour.minus(15, ChronoUnit.MINUTES)

        private val data = mapOf<String, List<Pair<OffsetDateTime, Int>>>(
            "hashtag" to listOf(
                shiftedZeroHour.minusHours(3).odt() to 13,
                shiftedZeroHour.odt() to 3,
                shiftedZeroHour.minusHours(2).odt() to 5,
                shiftedZeroHour.minusHours(1).odt() to 4,
                shiftedZeroHour.minusHours(5).odt() to 0,
                shiftedZeroHour.minusHours(4).odt() to 82,
            ),
            "empty" to listOf()
        )

        private fun Instant.odt() = atOffset(ZoneOffset.UTC)
        private fun Instant.minusHours(hours: Long) = minus(hours, ChronoUnit.HOURS)
    }
}