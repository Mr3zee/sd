package sysoev.sd.api.di

import io.ktor.client.engine.cio.*
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.logger.PrintLogger
import org.koin.dsl.module
import org.koin.environmentProperties
import sysoev.sd.api.client.TwitterHttpClient
import sysoev.sd.api.client.TwitterHttpClientImpl
import sysoev.sd.api.credentials.AppProperties

fun setupKoin() = startKoin {
    logger(PrintLogger(Level.INFO))

    val module = module {
        single {
            AppProperties(getProperty("TWITTER_API_TOKEN"))
        }
        single {
            CIO.create()
        }
        factory<TwitterHttpClient> {
            TwitterHttpClientImpl()
        }
    }

    modules(module)

    environmentProperties()
}
