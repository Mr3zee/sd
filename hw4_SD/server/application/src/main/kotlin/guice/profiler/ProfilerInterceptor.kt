package guice.profiler

import common.profiler.MethodInfo
import common.profiler.ProfilerAnalysis
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import mu.KotlinLogging
import org.aopalliance.intercept.MethodInterceptor
import org.aopalliance.intercept.MethodInvocation
import org.koin.core.component.KoinComponent
import org.koin.core.time.measureDuration

class ProfilerInterceptor(
    private val profilerServiceUrl: String,
    private val methodPackage: String?
) : MethodInterceptor, KoinComponent {
    private val logger = KotlinLogging.logger("ProfilerInterceptor")

    private val profilerChannel = Channel<ProfilerAnalysis>(Channel.UNLIMITED)
    private val scope = CoroutineScope(Dispatchers.IO)

    private val client by lazy {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json()
            }
        }
    }

    init {
        logger.info { "Starting ProfilerInterceptor, initializing channel${methodPackage?.let { " for $it package" } ?: ""}" }

        scope.launch {
            profilerChannel.consumeEach {
                reportInvocation(it)
            }
        }
    }

    private suspend fun reportInvocation(analysis: ProfilerAnalysis) {
        logger.debug { "Sending ${analysis.methodName()} invocation data to $profilerServiceUrl" }

        runCatching {
            client.post("$profilerServiceUrl/api/invocation") {
                headers {
                    append(HttpHeaders.ContentType, ContentType.Application.Json)
                }

                setBody(analysis)

                this.expectSuccess
            }
        }.getOrElse {
            logger.warn(it) { "Failed to send ${analysis.methodName()} data," }
        }
    }

    private fun ProfilerAnalysis.methodName() = "${method.declaringClassName}::${method.name}"

    override fun invoke(invocation: MethodInvocation): Any? {
        if (methodPackage != null && invocation.method.declaringClass.packageName != methodPackage) {
            return invocation.proceed()
        }

        var result: Any? = Unit

        val duration = measureDuration {
            result = invocation.proceed()
        }

        profilerChannel.trySend(
            ProfilerAnalysis(
                duration = duration,
                method = MethodInfo(
                    name = invocation.method.name,
                    declaringClassName = invocation.method.declaringClass.simpleName,
                    packageName = invocation.method.declaringClass.packageName,
                )
            )
        )

        return result
    }
}
