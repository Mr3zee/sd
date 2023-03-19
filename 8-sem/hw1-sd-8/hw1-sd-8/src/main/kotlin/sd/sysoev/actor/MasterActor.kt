package sd.sysoev.actor

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.consumeEach

class MasterActor(
    scope: CoroutineScope,
    private val children: List<ChildActor>,
    waitForFirstRequest: Boolean = true,
) : SearchActor {
    private var gate: CompletableDeferred<Unit>? = if (waitForFirstRequest) CompletableDeferred() else null

    override val sendChannel: SendChannel<SearchRequest> = Channel<SearchRequest>(Channel.UNLIMITED).apply {
        scope.launch {
            for (request in this@apply) {
                gate = gate?.let { if (!it.isCompleted) it else null } ?: CompletableDeferred()

                for (child in children) {
                    child.sendChannel.send(request)
                    println("master send to ${child.name}: ${request.query}")
                }

                gate!!.complete(Unit)
            }
        }
    }

    private val internalReceiveChannel = Channel<SearchResult>(Channel.UNLIMITED).apply {
        for (child in children) {
            scope.launch {
                try {
                    for (it in child.receiveChannel) {
                        send(it)
                        println("master received from ${child.name}: $it")
                    }
                } catch (e : CancellationException) {
                    // ignore
                }
            }
        }
    }

    override val receiveChannel = internalReceiveChannel

    suspend fun awaitAllAndClose(timeoutMillis: Long = 3000): List<SearchResult> = buildList {
        try {
            withTimeout(timeoutMillis) {
                launch {
                    internalReceiveChannel.consumeEach {
                        add(it)
                    }
                }

                gate?.await()

                children.forEach {
                    it.closeForSendAndAwaitCompleted()
                }

                internalReceiveChannel.close()
            }
        } catch (e: CancellationException) {
            // ignore
        } finally {
            close()
        }
    }

    override fun close() {
        children.forEach { it.close() }
        super.close()
    }
}

fun CoroutineScope.createMasterActor(children: List<ChildActor>) = MasterActor(this, children)
