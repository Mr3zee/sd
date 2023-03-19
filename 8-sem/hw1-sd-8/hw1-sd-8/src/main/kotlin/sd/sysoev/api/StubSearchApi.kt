package sd.sysoev.api

import kotlinx.coroutines.delay

// markers for DI
interface YandexApi : SearchApi
interface GoogleApi : SearchApi
interface BingApi : SearchApi

class StubSearchApi(
    private val delayMillis: Long = 0,
    private val response: (String) -> List<String>,
) : YandexApi, GoogleApi, BingApi {
    override suspend fun search(query: String, limit: Int): List<String> {
        delay(delayMillis)
        return response(query).take(limit)
    }
}
