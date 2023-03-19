package sd.sysoev

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.test.KoinTest
import org.koin.test.junit5.KoinTestExtension
import org.koin.test.mock.declare
import sd.sysoev.actor.SearchResult
import sd.sysoev.api.GoogleApi
import sd.sysoev.api.StubSearchApi
import sd.sysoev.plugins.routing
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientCN
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation as ServerCN

class ApplicationTest : KoinTest {
    @Suppress("unused")
    @JvmField
    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create {
        defaultKoinConfig()
    }

    @Test
    fun testNoTimeout() = testApplication {
        application {
            install(ServerCN) {
                json()
            }

            routing(timeoutMillis = 3000)
        }

        val client = createClient {
            install(ClientCN) {
                json()
            }
        }

        client.get("/?query=hello").apply {
            assertEquals(HttpStatusCode.OK, status)

            val response: List<SearchResult> = body()
            val succeeded = response.filterIsInstance<SearchResult.Success>().flatMap { it.result }.sorted()

            assertContentEquals(
                expected = listOf(
                    "bing-0-hello",
                    "bing-1-hello",
                    "bing-2-hello",
                    "bing-3-hello",
                    "bing-4-hello",
                    "google-0-hello",
                    "google-1-hello",
                    "google-2-hello",
                    "google-3-hello",
                    "google-4-hello",
                    "yandex-0-hello",
                    "yandex-1-hello",
                    "yandex-2-hello",
                    "yandex-3-hello",
                    "yandex-4-hello",
                ),
                actual = succeeded,
            )

            assertTrue { response.filterIsInstance<SearchResult.Failure>().isEmpty() }
        }
    }

    @Test
    fun testGoogleTimeout() = testApplication {
        application {
            install(ServerCN) {
                json()
            }

            routing(timeoutMillis = 3000)
        }

        declare<GoogleApi> {
            StubSearchApi(delayMillis = 4000) { fail("Should've failed with timeout") }
        }

        val client = createClient {
            install(ClientCN) {
                json()
            }
        }

        client.get("/?query=hello").apply {
            assertEquals(HttpStatusCode.OK, status)

            val response: List<SearchResult> = body()
            val succeeded = response.filterIsInstance<SearchResult.Success>().flatMap { it.result }.sorted()

            assertContentEquals(
                expected = listOf(
                    "bing-0-hello",
                    "bing-1-hello",
                    "bing-2-hello",
                    "bing-3-hello",
                    "bing-4-hello",
                    "yandex-0-hello",
                    "yandex-1-hello",
                    "yandex-2-hello",
                    "yandex-3-hello",
                    "yandex-4-hello",
                ),
                actual = succeeded,
            )

            assertTrue { response.filterIsInstance<SearchResult.Failure>().isEmpty() }
        }
    }
}
