package sysoev.sd.api.test

import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.koin.test.mock.declare
import sysoev.sd.api.client.TwitterGranularity
import sysoev.sd.api.client.TwitterHttpClientImpl
import sysoev.sd.api.client.TwitterResponse
import sysoev.sd.api.client.TwitterResponseData
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


class TwitterHttpClientTest : BaseTwitterTest() {
    @Test
    fun `test client request is correct`() = runBlocking {
        val hashtag = "hashtag"
        val end = Instant.now().atOffset(ZoneOffset.UTC)
        val start = Instant.now().minus(2, ChronoUnit.HOURS).atOffset(ZoneOffset.UTC)

        val expectedResponse = TwitterResponse(
            data = listOf(
                TwitterResponseData(
                    start = start,
                    end = end,
                    tweetCount = 100,
                )
            )
        )

        declare {
            MockEngine.create {
                addHandler {
                    val url = it.url
                    assertEquals("https://api.twitter.com", url.protocolWithAuthority)
                    assertEquals("/2/tweets/counts/recent", url.encodedPath)

                    val parameters = url.parameters
                    assertEquals("#$hashtag", parameters["query"], "Wrong query parameter")
                    assertEquals(start.toISO8601(), parameters["start_time"], "Wrong start_date parameter")
                    assertEquals(end.toISO8601(), parameters["end_time"], "Wrong end_date parameter")
                    assertEquals("hour", parameters["granularity"], "Wrong granular parameter")

                    respond(
                        content = Json.encodeToString(expectedResponse),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
            }
        }

        val client = TwitterHttpClientImpl()

        val actualResponse = client.sendRequestToTwitterCountApi(
            hashtag = hashtag,
            start = start,
            end = end,
            granularity = TwitterGranularity.HOUR
        )

        assertEquals(expectedResponse, actualResponse)
    }

    private fun OffsetDateTime.toISO8601() = format(DateTimeFormatter.ISO_DATE_TIME)
}