package profiler.modules

import org.koin.dsl.module
import profiler.services.InvocationService

val ServicesModule = module {
    single { InvocationService() }
}