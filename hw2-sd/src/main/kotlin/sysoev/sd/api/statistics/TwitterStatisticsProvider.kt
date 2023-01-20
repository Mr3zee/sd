package sysoev.sd.api.statistics


import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import sysoev.sd.api.client.TwitterGranularity
import sysoev.sd.api.client.TwitterHttpClient
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit


class TwitterStatisticsProvider : KoinComponent {
    private val client by inject<TwitterHttpClient>()

    suspend fun requestStatisticsByHours(hashtag: String, lastNHours: Int): TwitterStatistics {
        if (lastNHours !in 1..24) {
            throw TwitterStatisticsInvalidRangeException()
        }

        val (start, end) = getStartAndEndTime(lastNHours)

        val statistics = client.sendRequestToTwitterCountApi(
            hashtag = hashtag,
            start = start,
            end = end,
            granularity = TwitterGranularity.HOUR,
        )

        val byHours = statistics.data
            .sortedBy { ChronoUnit.HOURS.between(end, it.end) }
            .map { it.tweetCount }
            .reversed()

        return TwitterStatistics(byHours)
    }

    private fun getStartAndEndTime(lastNHours: Int): Pair<OffsetDateTime, OffsetDateTime> {
        val now = Instant.now().truncatedTo(ChronoUnit.HOURS)
        val start = now.minus(lastNHours.toLong(), ChronoUnit.HOURS).atCET()
        return start to now.atCET()
    }

    private fun Instant.atCET() = OffsetDateTime.from(atZone(ZoneId.of("CET")))
}

class TwitterStatisticsInvalidRangeException : Exception("Only values in [1, 24] range are allowed")

@Serializable
data class TwitterStatistics(
    val byHours: List<Int>
)
