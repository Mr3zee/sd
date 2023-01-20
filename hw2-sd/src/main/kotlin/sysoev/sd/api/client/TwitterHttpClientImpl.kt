@file:UseSerializers(OffsetDateTimeSerializer::class)

package sysoev.sd.api.client

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import sysoev.sd.api.credentials.AppProperties
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter


interface TwitterHttpClient {
    suspend fun sendRequestToTwitterCountApi(
        hashtag: String,
        start: OffsetDateTime,
        end: OffsetDateTime,
        granularity: TwitterGranularity,
    ): TwitterResponse
}

class TwitterHttpClientImpl : TwitterHttpClient, KoinComponent {
    private val properties by inject<AppProperties>()
    private val engine by inject<HttpClientEngine>()

    private val client by lazy {
        HttpClient(engine) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }
    }

    override suspend fun sendRequestToTwitterCountApi(
        hashtag: String,
        start: OffsetDateTime,
        end: OffsetDateTime,
        granularity: TwitterGranularity
    ): TwitterResponse {
        return client.request(COUNT_URL) {
            headers {
                bearerAuth(properties.twitterApiToken)
            }

            parameter("query", "#$hashtag")
            parameter("start_time", start.toISO8601())
            parameter("end_time", end.toISO8601())
            parameter(TwitterGranularity.URL_PARAMETER, granularity.urlName)
        }.body()
    }

    companion object {
        private const val COUNT_URL = "https://api.twitter.com/2/tweets/counts/recent"
    }
}

@Serializable
data class TwitterResponse(
    val data: List<TwitterResponseData>
)

@Serializable
data class TwitterResponseData(
    val start: OffsetDateTime,
    val end: OffsetDateTime,
    @SerialName("tweet_count")
    val tweetCount: Int
)

class OffsetDateTimeSerializer : KSerializer<OffsetDateTime> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("OffsetDateTime", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): OffsetDateTime {
        return OffsetDateTime.parse(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: OffsetDateTime) {
        encoder.encodeString(value.toISO8601())
    }
}

@Suppress("unused")
enum class TwitterGranularity {
    HOUR, MINUTE, DAY;

    val urlName = name.lowercase()

    companion object {
        const val URL_PARAMETER = "granularity"
    }
}

private fun OffsetDateTime.toISO8601() = format(DateTimeFormatter.ISO_DATE_TIME)
