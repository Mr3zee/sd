package modules

import com.google.inject.Injector
import org.koin.dsl.module
import services.planner.PlannerService
import services.planner.PlannerServiceImpl
import services.task.TaskService
import services.task.TaskServiceImpl

val ServicesModule = module {
    single<PlannerService> { get<Injector>().getInstance(PlannerServiceImpl::class.java)!! }
    single<TaskService> { get<Injector>().getInstance(TaskServiceImpl::class.java)!! }
}
