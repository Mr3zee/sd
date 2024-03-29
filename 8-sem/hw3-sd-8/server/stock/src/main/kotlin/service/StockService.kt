package service

import DealResult
import StockInfo
import db.StockTables
import db.util.tx
import org.jetbrains.exposed.sql.*
import org.koin.dsl.module


val serviceModule = module {
    single<AdminStockService> { StockServiceImpl() }
}

interface AdminStockService : StockService {
    suspend fun addCompany(companyName: String, code: String, stockValue: Double, stockQuantity: Int)

    suspend fun updateCompany(code: String, stockValue: Double? = null, stockQuantity: Int? = null)

    suspend fun addStokes(code: String, stockQuantity: Int)

    suspend fun clear()
}

class StockServiceImpl : AdminStockService {
    override suspend fun clear() {
        tx {
            StockTables.Stokes.deleteAll()
        }
    }

    override suspend fun addCompany(companyName: String, code: String, stockValue: Double, stockQuantity: Int) {
        tx {
            StockTables.Stokes.insert {
                it[StockTables.Stokes.companyName] = companyName
                it[StockTables.Stokes.code] = code
                it[StockTables.Stokes.stockValue] = stockValue
                it[StockTables.Stokes.stockQuantity] = stockQuantity
            }
        }
    }

    override suspend fun updateCompany(code: String, stockValue: Double?, stockQuantity: Int?) {
        tx {
            StockTables.Stokes.update(
                where = { StockTables.Stokes.code.eq(code) }
            ) {
                stockValue?.let { value -> it[StockTables.Stokes.stockValue] = value }
                stockQuantity?.let { quantity -> it[StockTables.Stokes.stockQuantity] = quantity }
            }
        }
    }

    override suspend fun addStokes(code: String, stockQuantity: Int) {
        // Hello, race conditions!
        val info = getStockInfo(code) ?: return
        updateCompany(code, stockQuantity = info.stockQuantity + stockQuantity)
    }

    override suspend fun getStockInfo(code: String): StockInfo? {
        return tx {
            StockTables.Stokes.select {
                StockTables.Stokes.code.eq(code)
            }.map {
                StockInfo(
                    code = it[StockTables.Stokes.code],
                    stockValue = it[StockTables.Stokes.stockValue],
                    stockQuantity = it[StockTables.Stokes.stockQuantity]
                )
            }.singleOrNull()
        }
    }

    override suspend fun buyStocks(code: String, quantity: Int): DealResult {
        // Hello, race conditions!
        val available = getStockInfo(code)?.stockQuantity
            ?: return DealResult.Failure("Invalid code")

        if (available < quantity) {
            return DealResult.Failure("no enough stokes")
        }

        updateCompany(code, stockQuantity = available - quantity)

        return DealResult.Success
    }

    override suspend fun sellStocks(code: String, quantity: Int): DealResult {
        // Hello, race conditions!
        val available = getStockInfo(code)?.stockQuantity
            ?: return DealResult.Failure("Invalid stock code")

        updateCompany(code, stockQuantity = available + quantity)

        return DealResult.Success
    }

    override suspend fun getAllStocks(): List<StockInfo> {
        return tx {
            StockTables.Stokes.selectAll().map {
                StockInfo(
                    code = it[StockTables.Stokes.code],
                    stockValue = it[StockTables.Stokes.stockValue],
                    stockQuantity = it[StockTables.Stokes.stockQuantity]
                )
            }
        }
    }
}
