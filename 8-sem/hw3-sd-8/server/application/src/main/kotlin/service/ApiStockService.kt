package service

import DealResult
import StockInfo
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import org.koin.dsl.module

val stockModule = module {
    single<StockService> { ApiStockService("localhost", 8081) }
}

class ApiStockService(host: String, port: Int) : StockService {
    private val client by lazy {
        HttpClient(CIO) {
            defaultRequest {
                url {
                    this.host = host
                    this.port = port
                }
            }

            install(ContentNegotiation) {
                json()
            }
        }
    }

    override suspend fun getStockInfo(code: String): StockInfo? {
        return client.get("/api/info") {
            parameter("code", code)
        }.run {
            if (status != HttpStatusCode.OK) null else body()
        }
    }

    override suspend fun getAllStocks(): List<StockInfo> {
        return client.get("/api/all").body()
    }

    override suspend fun buyStocks(code: String, quantity: Int): DealResult {
        return client.put("/api/buy") {
            parameter("code", code)
            parameter("quantity", quantity)
        }.body()
    }

    override suspend fun sellStocks(code: String, quantity: Int): DealResult {
        return client.delete("/api/sell") {
            parameter("code", code)
            parameter("quantity", quantity)
        }.body()
    }
}
