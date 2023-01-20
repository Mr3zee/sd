package guice.modules

import com.google.inject.AbstractModule
import com.google.inject.Singleton
import services.task.TaskService
import services.task.TaskServiceImpl

class TaskServiceModule : AbstractModule() {
    override fun configure() {
        bind(TaskService::class.java).to(TaskServiceImpl::class.java).`in`(Singleton::class.java)
    }
}
