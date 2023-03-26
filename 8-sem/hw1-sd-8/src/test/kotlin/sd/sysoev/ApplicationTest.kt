package sd.sysoev

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import kotlinx.coroutines.delay
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.core.qualifier.named
import org.koin.test.KoinTest
import org.koin.test.junit5.KoinTestExtension
import org.koin.test.mock.declare
import sd.sysoev.actor.SearchResult
import sd.sysoev.plugins.routing
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue
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
    fun testNoTimeout() = withTestConfig { client ->
        externalServices {
            configureExternal("https://www.google.com", "google", 0)
            configureExternal("https://www.yandex.ru", "yandex", 0)
            configureExternal("https://www.bing.com", "bing", 0)
        }

        withApiClient()

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
    fun testGoogleTimeout() = withTestConfig { client ->
        externalServices {
            configureExternal("https://www.google.com", "google", 4000)
            configureExternal("https://www.yandex.ru", "yandex", 0)
            configureExternal("https://www.bing.com", "bing", 0)
        }

        withApiClient()

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

    private fun withTestConfig(
        routingTimeout: Long = 3000,
        appConfig: Application.() -> Unit = {},
        body: suspend ApplicationTestBuilder.(HttpClient) -> Unit,
    ) = testApplication {
        application {
            install(ServerCN) {
                json()
            }

            routing(timeoutMillis = routingTimeout)

            appConfig()
        }

        val client = createClient {
            install(ClientCN) {
                json()
            }
        }

        body(client)
    }

    private fun ExternalServicesBuilder.configureExternal(url: String, prefix: String, delayMillis: Long) {
        hosts(url) {
            install(ServerCN) {
                json()
            }

            routing {
                get("/") {
                    delay(delayMillis)
                    val query = call.parameters["query"]!!
                    call.respond(List(5) { "$prefix-$it-$query" })
                }
            }
        }
    }

    private fun ApplicationTestBuilder.withApiClient() {
        declare(named("api-client")) {
            createClient {
                install(ClientCN) {
                    json()
                }
            }
        }
    }
}
