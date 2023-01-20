package profiler.services

import common.profiler.MethodInfo
import common.profiler.ProfilerAnalysis
import common.profiler.ProfilerMethodStatistics
import common.profiler.ProfilerStatistics
import db.util.tx
import kotlinx.coroutines.channels.Channel
import mu.KotlinLogging
import org.jetbrains.exposed.sql.*
import profiler.db.Tables
import java.util.concurrent.atomic.AtomicInteger

class InvocationService {
    private val logger = KotlinLogging.logger("InvocationService")
    private val index = AtomicInteger(0)
    private val channels = mutableMapOf<Int, Channel<ProfilerStatistics>>()

    suspend fun registerInvocation(analysis: ProfilerAnalysis) {
        tx {
            val id = Tables.ProfilerMethods.insertIgnoreAndGetId {
                it[packageName] = analysis.method.packageName
                it[declaringClassName] = analysis.method.declaringClassName
                it[methodName] = analysis.method.name
            } ?: run {
                Tables.ProfilerMethods
                    .slice(Tables.ProfilerMethods.id)
                    .select {
                        (Tables.ProfilerMethods.packageName eq analysis.method.packageName)
                            .and(Tables.ProfilerMethods.declaringClassName eq analysis.method.declaringClassName)
                            .and(Tables.ProfilerMethods.methodName eq analysis.method.name)
                    }
                    .single()[Tables.ProfilerMethods.id]
            }

            Tables.MethodInvocations.insert {
                it[methodId] = id
                it[durationMillis] = analysis.duration
            }
        }

        notifySubscribers()
    }

    private suspend fun notifySubscribers() {
        if (channels.isNotEmpty()) {
            val statistics = getStatistics()
            channels.values.forEach {
                it.send(statistics)
            }
        }
    }

    suspend fun subscribeOnUpdates(): Pair<Int, Channel<ProfilerStatistics>> {
        val id = index.getAndIncrement()
        logger.debug { "New listener with id $id" }
        return id to Channel<ProfilerStatistics>(Channel.UNLIMITED).also {
            channels[id] = it
            it.send(getStatistics())
        }
    }

    fun unsubscribeFromUpdates(id: Int) {
        logger.debug { "Unsubscribed listener $id" }
        channels[id]?.close()
        channels -= id
    }

    suspend fun getStatistics(): ProfilerStatistics {
        return tx {
            val count = Tables.MethodInvocations.durationMillis.count().alias("count")
            val sum = Tables.MethodInvocations.durationMillis.sum().alias("sum")
            val avg = Tables.MethodInvocations.durationMillis.avg().alias("avg")

            Tables.ProfilerMethods
                .leftJoin(Tables.MethodInvocations, { Tables.ProfilerMethods.id }, { methodId })
                .slice(
                    Tables.ProfilerMethods.packageName,
                    Tables.ProfilerMethods.declaringClassName,
                    Tables.ProfilerMethods.methodName,
                    Tables.MethodInvocations.methodId,
                    count,
                    sum,
                    avg,
                )
                .selectAll()
                .groupBy(
                    Tables.MethodInvocations.methodId,
                    Tables.ProfilerMethods.packageName,
                    Tables.ProfilerMethods.declaringClassName,
                    Tables.ProfilerMethods.methodName,
                )
                .map {
                    ProfilerMethodStatistics(
                        methodInfo = MethodInfo(
                            packageName = it[Tables.ProfilerMethods.packageName],
                            declaringClassName = it[Tables.ProfilerMethods.declaringClassName],
                            name = it[Tables.ProfilerMethods.methodName],
                        ),
                        numberOfInvocations = it[count],
                        summaryExecutionTimeInMillis = it[sum] ?: 0.0,
                        averageExecutionTimeInMillis = it[avg]?.toDouble() ?: 0.0,
                    )
                }
        }.let { ProfilerStatistics(it) }
    }
}
