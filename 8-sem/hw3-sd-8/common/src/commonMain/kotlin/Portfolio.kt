import kotlinx.serialization.Serializable

@Serializable
data class Portfolio(
    val stocks: List<StockInfo>
)

@Serializable
data class AccountInfo(
    val balance: Double,
    val stockValue: Double,
)
