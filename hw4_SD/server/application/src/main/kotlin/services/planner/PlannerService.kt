package services.planner

import common.api.Planner
import common.api.Task
import db.Tables
import db.util.tx
import guice.profiler.RunWithProfiling
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update

interface PlannerService {
    suspend fun addPlanner(planner: Planner): Int

    suspend fun updatePlanner(planner: Planner)

    suspend fun archivePlanner(plannerId: Int, value: Boolean)

    suspend fun presentPlanners(): List<Planner>

    suspend fun archivedPlanners(): List<Planner>


}

open class PlannerServiceImpl : PlannerService {
    @RunWithProfiling
    override suspend fun addPlanner(planner: Planner): Int {
        return tx {
            Tables.Planners.insert {
                it[name] = planner.name
                it[description] = planner.description
            } get Tables.Planners.id
        }.value
    }

    @RunWithProfiling
    override suspend fun updatePlanner(planner: Planner) {
        tx {
            Tables.Planners.update(
                where = {
                    Tables.Planners.id.eq(planner.id)
                }
            ) {
                it[name] = planner.name
                it[description] = planner.description
            }
        }
    }

    @RunWithProfiling
    override suspend fun archivePlanner(plannerId: Int, value: Boolean) {
        tx {
            Tables.Planners.update(
                where = {
                    Tables.Planners.id.eq(plannerId)
                }
            ) {
                it[archived] = value
            }
        }
    }

    @RunWithProfiling
    override suspend fun presentPlanners(): List<Planner> {
        return selectPlanners(archived = false)
    }

    @RunWithProfiling
    override suspend fun archivedPlanners(): List<Planner> {
        return selectPlanners(archived = true)
    }

    private suspend fun selectPlanners(archived: Boolean): List<Planner> {
        return tx {
            Tables.Planners.join(Tables.Tasks, JoinType.LEFT, Tables.Planners.id, Tables.Tasks.plannerId)
                .select { Tables.Planners.archived.eq(archived) }
                .map {
                    val planner = Planner(
                        id = it[Tables.Planners.id].value,
                        name = it[Tables.Planners.name],
                        description = it[Tables.Planners.description],
                        archived = archived,
                        tasks = emptyList()
                    )
                    val task = it.getOrNull(Tables.Tasks.id)?.let { _ ->
                        Task(
                            id = it[Tables.Tasks.id].value,
                            name = it[Tables.Tasks.name],
                            description = it[Tables.Tasks.description],
                            done = it[Tables.Tasks.done],
                            plannerId = it[Tables.Tasks.plannerId].value,
                        )
                    }
                    planner to task
                }
                .groupBy(keySelector = { it.first }, valueTransform = { it.second })
                .map { (planner, tasks) ->
                    planner.copy(tasks = tasks.filterNotNull())
                }
        }
    }
}
