package guice.modules

import com.google.inject.AbstractModule
import com.google.inject.matcher.Matchers
import guice.profiler.ProfilerInterceptor
import guice.profiler.RunWithProfiling

class ProfilerModule(
    private val profilerServiceUrl: String,
    private val packageToScan: String?
) : AbstractModule() {
    override fun configure() {
        bindInterceptor(
            Matchers.any(),
            Matchers.annotatedWith(RunWithProfiling::class.java),
            ProfilerInterceptor(profilerServiceUrl, packageToScan)
        )
    }
}
