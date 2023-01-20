package common.profiler

import kotlinx.serialization.Serializable
import org.koin.core.time.TimeInMillis

@Serializable
data class ProfilerAnalysis(
    val duration: TimeInMillis,
    val method: MethodInfo
)

@Serializable
data class MethodInfo(
    val name: String,
    val declaringClassName: String,
    val packageName: String
)
