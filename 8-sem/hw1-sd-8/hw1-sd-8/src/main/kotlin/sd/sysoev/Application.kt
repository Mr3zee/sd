package sd.sysoev

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import org.koin.core.KoinApplication
import org.koin.ktor.plugin.Koin
import sd.sysoev.api.BingApi
import sd.sysoev.api.GoogleApi
import sd.sysoev.api.StubSearchApi
import sd.sysoev.api.YandexApi
import sd.sysoev.plugins.routing

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }

    configureKoin(defaultKoinConfig)

    routing(timeoutMillis = 6000)
}

fun Application.configureKoin(config: KoinApplication.() -> Unit) {
    install(Koin) {
        config()
    }
}

val defaultKoinConfig: KoinApplication.() -> Unit = {
    val module = org.koin.dsl.module {
        single<GoogleApi> {
            StubSearchApi { query ->
                List(5) { "google-$it-$query" }
            }
        }

        single<YandexApi> {
            StubSearchApi { query ->
                List(5) { "yandex-$it-$query" }
            }
        }

        single<BingApi> {
            StubSearchApi { query ->
                List(5) { "bing-$it-$query" }
            }
        }
    }

    modules(module)
}
