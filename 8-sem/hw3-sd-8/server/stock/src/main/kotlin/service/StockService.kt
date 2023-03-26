package service

import DealResult
import StockInfo
import db.StockTables
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import org.koin.dsl.module


val serviceModule = module {
    single<StockService> { StockServiceImpl() }
}

interface StockService {
    fun addCompany(companyName: String, code: String, stockValue: Double, stockQuantity: Int)

    fun updateCompany(code: String, stockValue: Double? = null, stockQuantity: Int? = null)

    fun addStokes(code: String, stockQuantity: Int)

    fun getStockInfo(code: String): StockInfo?

    fun buyStocks(code: String, quantity: Int): DealResult
}

class StockServiceImpl : StockService {
    override fun addCompany(companyName: String, code: String, stockValue: Double, stockQuantity: Int) {
        StockTables.Stokes.insert {
            it[StockTables.Stokes.companyName] = companyName
            it[StockTables.Stokes.code] = code
            it[StockTables.Stokes.stockValue] = stockValue
            it[StockTables.Stokes.stockQuantity] = stockQuantity
        }
    }

    override fun updateCompany(code: String, stockValue: Double?, stockQuantity: Int?) {
        StockTables.Stokes.update(
            where = { StockTables.Stokes.code.eq(code) }
        ) {
            stockValue?.let { value -> it[StockTables.Stokes.stockValue] = value }
            stockQuantity?.let { quantity -> it[StockTables.Stokes.stockQuantity] = quantity }
        }
    }

    override fun addStokes(code: String, stockQuantity: Int) {
        // Hello, race conditions!
        val info = getStockInfo(code) ?: return
        updateCompany(code, stockQuantity = info.stockQuantity + stockQuantity)
    }

    override fun getStockInfo(code: String): StockInfo? {
        return StockTables.Stokes.select {
            StockTables.Stokes.code.eq(code)
        }.map {
            StockInfo(
                code = it[StockTables.Stokes.code],
                stockValue = it[StockTables.Stokes.stockValue],
                stockQuantity = it[StockTables.Stokes.stockQuantity]
            )
        }.singleOrNull()
    }

    override fun buyStocks(code: String, quantity: Int): DealResult {
        // Hello, race conditions!
        val available = getStockInfo(code)?.stockQuantity
            ?: return DealResult.Failure("Invalid code")

        if (available < quantity) {
            return DealResult.Failure("no enough stokes")
        }

        updateCompany(code, stockQuantity = available - quantity)

        return DealResult.Success
    }
}
