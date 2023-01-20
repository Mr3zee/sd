package guice.modules

import com.google.inject.AbstractModule
import com.google.inject.Singleton
import services.planner.PlannerService
import services.planner.PlannerServiceImpl


class PlannerServiceModule : AbstractModule() {
    override fun configure() {
        bind(PlannerService::class.java).to(PlannerServiceImpl::class.java).`in`(Singleton::class.java)
    }
}
