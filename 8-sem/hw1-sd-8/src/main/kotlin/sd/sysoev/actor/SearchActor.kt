package sd.sysoev.actor

import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.serialization.Serializable

interface SearchActor : AutoCloseable {
    val sendChannel: SendChannel<SearchRequest>

    val receiveChannel: ReceiveChannel<SearchResult>

    override fun close() {
        sendChannel.close()
        receiveChannel.cancel()
    }
}

data class SearchRequest(
    val query: String,
    val timeoutMillis: Long? = null,
)

@Serializable
sealed interface SearchResult {
    @Serializable
    data class Success(val result: List<String>) : SearchResult

    @Serializable
    data class Failure(val error: String?) : SearchResult
}
