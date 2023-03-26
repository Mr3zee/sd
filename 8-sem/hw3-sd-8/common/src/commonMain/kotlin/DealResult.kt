import kotlinx.serialization.Serializable

@Serializable
sealed interface DealResult {
    @Serializable
    object Success : DealResult

    @Serializable
    class Failure(val reason: String) : DealResult
}
