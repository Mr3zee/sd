package service

import AccountInfo
import DealResult
import Portfolio
import StockInfo
import db.UserTables
import db.util.tx
import org.jetbrains.exposed.sql.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.dsl.module

interface UserService {
    suspend fun register(): Int

    suspend fun login(id: Int): Boolean

    suspend fun deposit(id: Int, amount: Double)

    suspend fun getPortfolio(id: Int): Portfolio

    suspend fun getAccountInfo(id: Int): AccountInfo?

    suspend fun getAvailableStocks(): List<StockInfo>

    suspend fun buyStock(id: Int, code: String, amount: Int): DealResult

    suspend fun sellStock(id: Int, code: String, amount: Int): DealResult
}

val userModule = module {
    single<UserService> { UserServiceImpl() }
}

class UserServiceImpl : UserService, KoinComponent {
    private val stockApi by inject<StockService>()

    override suspend fun login(id: Int): Boolean {
        return tx {
            !UserTables.User.select {
                UserTables.User.id.eq(id)
            }.empty()
        }
    }

    override suspend fun register(): Int {
        return tx {
            UserTables.User.insertAndGetId {
                it[balance] = 0.0
            }.value
        }
    }

    override suspend fun deposit(id: Int, amount: Double) {
        tx {
            val balance = UserTables.User
                .select { UserTables.User.id.eq(id) }
                .map { it[UserTables.User.balance] }
                .singleOrNull() ?: return@tx

            UserTables.User.update(
                where = { UserTables.User.id.eq(id) }
            ) {
                it[UserTables.User.balance] = balance + amount
            }
        }
    }

    override suspend fun getPortfolio(id: Int): Portfolio {
        val all = stockApi.getAllStocks().associate { it.code to it.stockValue }

        return tx {
            UserTables.Portfolio.select {
                UserTables.Portfolio.user.eq(id)
            }.map {
                StockInfo(
                    code = it[UserTables.Portfolio.stockCode],
                    stockValue = all.getValue(it[UserTables.Portfolio.stockCode]),
                    stockQuantity = it[UserTables.Portfolio.amount],
                )
            }.let { Portfolio(it) }
        }
    }

    override suspend fun getAccountInfo(id: Int): AccountInfo? {
        val balance = tx {
            UserTables.User
                .select { UserTables.User.id.eq(id) }
                .map { it[UserTables.User.balance] }
                .singleOrNull()
        } ?: return null

        return getPortfolio(id).stocks.fold(0.0) { acc, stockInfo ->
            acc + stockInfo.stockValue * stockInfo.stockQuantity
        }.let {
            AccountInfo(balance, it)
        }
    }

    override suspend fun getAvailableStocks(): List<StockInfo> {
        return tx { stockApi.getAllStocks() }
    }

    override suspend fun buyStock(id: Int, code: String, amount: Int): DealResult {
        val info = stockApi.getStockInfo(code) ?: return DealResult.Failure("unknown stock")

        if (info.stockQuantity < amount) {
            return DealResult.Failure("not enough stocks")
        }

        val price = info.stockValue * amount

        val balance = getAccountInfo(id)?.balance ?: return DealResult.Failure("invalid user id")

        if (price > balance) {
            return DealResult.Failure("not enough money on account")
        }

        val result = stockApi.buyStocks(code, amount)

        if (result is DealResult.Success) {
            updateStockAmount(id, code, amount, balance - price)
        }

        return result
    }

    override suspend fun sellStock(id: Int, code: String, amount: Int): DealResult {
        val info = stockApi.getStockInfo(code) ?: return DealResult.Failure("unknown stock")

        val result = stockApi.sellStocks(code, amount)

        val price = info.stockValue * amount

        val balance = getAccountInfo(id)?.balance ?: return DealResult.Failure("invalid user id")

        if (result is DealResult.Success) {
            updateStockAmount(id, code, -amount, balance + price)
        }

        return result
    }

    private suspend fun updateStockAmount(id: Int, code: String, addStokeAmount: Int, newBalance: Double) {
        tx {
            UserTables.Portfolio.insertIgnore {
                it[user] = id
                it[stockCode] = code
                it[amount] = 0
            }

            val current = UserTables.Portfolio.select {
                UserTables.Portfolio.user.eq(id) and UserTables.Portfolio.stockCode.eq(code)
            }.map { it[UserTables.Portfolio.amount] }.single()

            UserTables.Portfolio.update(
                where = { UserTables.Portfolio.user.eq(id) and UserTables.Portfolio.stockCode.eq(code) }
            ) {
                it[amount] = current + addStokeAmount
            }

            UserTables.User.update(
                where = { UserTables.User.id.eq(id) }
            ) {
                it[balance] = newBalance
            }
        }
    }
}
