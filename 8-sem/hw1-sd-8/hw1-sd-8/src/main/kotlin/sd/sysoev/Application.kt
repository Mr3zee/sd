package sd.sysoev

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.koin.core.KoinApplication
import org.koin.core.qualifier.named
import org.koin.ktor.plugin.Koin
import sd.sysoev.api.BingApi
import sd.sysoev.api.GoogleApi
import sd.sysoev.api.YandexApi
import sd.sysoev.plugins.routing
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientCN
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation as ServerCN

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(ServerCN) {
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
        single { GoogleApi() }
        single { YandexApi() }
        single { BingApi() }

        single(named("api-client")) {
            HttpClient(CIO) {
                install(ClientCN) {
                    json()
                }
            }
        }
    }

    modules(module)
}
