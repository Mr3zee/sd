package common.profiler

import kotlinx.serialization.Serializable
import org.koin.core.time.TimeInMillis

@Serializable
data class ProfilerStatistics(
    val methodStatistics: List<ProfilerMethodStatistics>
)

@Serializable
data class ProfilerMethodStatistics(
    val methodInfo: MethodInfo,
    val numberOfInvocations: Long,
    val summaryExecutionTimeInMillis: TimeInMillis,
    val averageExecutionTimeInMillis: TimeInMillis,
)