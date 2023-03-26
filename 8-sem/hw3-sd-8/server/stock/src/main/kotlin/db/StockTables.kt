package db

import db.connection.TableHolder
import org.jetbrains.exposed.dao.id.IntIdTable
import org.koin.dsl.bind
import org.koin.dsl.module

val dbModule = module {
    single { StockTables }.bind<TableHolder>()
}

object StockTables : TableHolder {
    object Stokes : IntIdTable() {
        val companyName = varchar("company_name", 256)
        val code = varchar("code", 16).uniqueIndex()
        val stockValue = double("stock_value")
        val stockQuantity = integer("stock_quantity")
    }
}
