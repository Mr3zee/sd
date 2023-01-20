package modules.guice

import com.google.inject.AbstractModule
import guice.modules.PlannerServiceModule
import guice.modules.ProfilerModule
import guice.modules.TaskServiceModule
import org.koin.dsl.bind
import org.koin.dsl.module
import server.ApplicationPropertyProvider


val GuiceModules = module {
    single {
        val paramsProvider = get<ApplicationPropertyProvider>()

        val host = paramsProvider.property("guice.profiler.service.host").getString()
        val port = paramsProvider.property("guice.profiler.service.port").getString().toInt()

        val packageToScan = paramsProvider.propertyOrNull("guice.profiler.packageToScan")?.getString()

        ProfilerModule(
            profilerServiceUrl = "$host:$port",
            packageToScan = packageToScan
        )
    }

    single { PlannerServiceModule() }.bind<AbstractModule>()

    single { TaskServiceModule() }.bind<AbstractModule>()
}
