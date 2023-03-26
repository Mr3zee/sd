import kotlinx.serialization.Serializable

@Serializable
data class StockInfo(
    val code: String,
    val stockValue: Double,
    val stockQuantity: Int,
)
