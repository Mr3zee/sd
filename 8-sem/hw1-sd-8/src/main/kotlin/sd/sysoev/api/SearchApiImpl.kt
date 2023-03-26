package sd.sysoev.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

sealed class SearchApiBase(private val url: String, apiClientName: String = "api-client"): SearchApi, KoinComponent {
    private val client by inject<HttpClient>(named(apiClientName))

    override suspend fun search(query: String, limit: Int): List<String> {
        return client.get("$url?query=$query").body()
    }
}

class YandexApi : SearchApiBase("https://www.yandex.ru")

class GoogleApi : SearchApiBase("https://www.google.com")

class BingApi : SearchApiBase("https://www.bing.com")
