package sysoev.sd.api

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import sysoev.sd.api.di.setupKoin
import sysoev.sd.api.statistics.TwitterStatisticsProvider

val prettyJson by lazy {
    Json {
        prettyPrint = true
    }
}

fun main() = runBlocking {
    val koin = setupKoin()

    val provider = TwitterStatisticsProvider()

    val statistics = provider.requestStatisticsByHours("hashtag", 4)

    println(prettyJson.encodeToString(statistics))

    koin.close()
}
