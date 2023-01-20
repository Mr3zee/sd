package modules.guice

import com.google.inject.AbstractModule
import com.google.inject.Guice
import guice.modules.ProfilerModule
import org.koin.dsl.module

val GuiceInjector = module {
    single {
        val profiler = get<ProfilerModule>()
        Guice.createInjector(listOf(profiler) + getAll<AbstractModule>())
    }
}
