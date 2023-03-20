package sd.sysoev.actor

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import sd.sysoev.api.SearchApi

class ChildActor(scope: CoroutineScope, api: SearchApi, val name: String) : SearchActor {
    private val internalReceiveChannel = Channel<SearchResult>(Channel.UNLIMITED)

    private var job: Job? = null

    override val sendChannel: SendChannel<SearchRequest> = Channel<SearchRequest>(Channel.UNLIMITED).apply {
        job = scope.launch {
            for (request in this@apply) {
                println("$name received: ${request.query}")
                val result = try {
                    if (request.timeoutMillis != null) {
                        withTimeout(request.timeoutMillis) {
                            api.search(request.query)
                        }
                    } else {
                        api.search(request.query)
                    }.let { SearchResult.Success(it) }
                } catch (_: TimeoutCancellationException) {
                    SearchResult.Failure(null)
                } catch (e: Exception) {
                    e.printStackTrace()
                    SearchResult.Failure(e.message)
                }

                internalReceiveChannel.send(result)
                println("$name sent: ${request.query}")
            }
        }
    }

    override val receiveChannel: ReceiveChannel<SearchResult> = internalReceiveChannel

    override fun close() {
        super.close()
        job?.cancel()
    }

    suspend fun closeForSendAndAwaitCompleted() {
        sendChannel.close()
        job?.join()
        internalReceiveChannel.close()
    }
}

fun CoroutineScope.createChildActor(name: String, api: SearchApi) = ChildActor(this, api, name)
