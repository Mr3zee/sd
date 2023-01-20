package services.task

import common.api.Task
import db.Tables
import db.util.tx
import guice.profiler.RunWithProfiling
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.update

interface TaskService {
    suspend fun addTask(task: Task): Int

    suspend fun updateTask(task: Task)

    suspend fun deleteTask(taskId: Int)
}

open class TaskServiceImpl : TaskService {
    @RunWithProfiling
    override suspend fun addTask(task: Task): Int {
        return tx {
            Tables.Tasks.insert {
                it[name] = task.name
                it[description] = task.description
                it[plannerId] = task.plannerId
            } get Tables.Tasks.id
        }.value
    }

    @RunWithProfiling
    override suspend fun updateTask(task: Task) {
        tx {
            Tables.Tasks.update(
                where = {
                    Tables.Tasks.id.eq(task.id)
                }
            ) {
                it[name] = task.name
                it[description] = task.description
                it[done] = task.done
                it[plannerId] = task.plannerId
            }
        }
    }

    @RunWithProfiling
    override suspend fun deleteTask(taskId: Int) {
        tx {
            Tables.Tasks.deleteWhere {
                Tables.Tasks.id.eq(taskId)
            }
        }
    }
}