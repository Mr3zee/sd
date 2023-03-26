package service

import DealResult
import StockInfo

interface StockService {
    suspend fun getStockInfo(code: String): StockInfo?

    suspend fun getAllStocks(): List<StockInfo>

    suspend fun buyStocks(code: String, quantity: Int): DealResult

    suspend fun sellStocks(code: String, quantity: Int): DealResult
}
