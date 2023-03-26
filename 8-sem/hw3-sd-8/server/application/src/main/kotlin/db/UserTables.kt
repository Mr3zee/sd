package db

import db.connection.TableHolder
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Table
import org.koin.dsl.bind
import org.koin.dsl.module

val dbModule = module {
    single { UserTables }.bind<TableHolder>()
}

object UserTables : TableHolder {
    object User : IntIdTable() {
        val balance = double("balance")
    }

    object Portfolio : Table() {
        val user = integer("user").references(User.id)
        val stockCode = varchar("stock-code", 16)
        val amount = integer("amount")

        init {
            uniqueIndex(user, stockCode)
        }
    }
}
