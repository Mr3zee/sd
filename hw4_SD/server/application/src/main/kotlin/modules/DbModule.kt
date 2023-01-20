package modules

import db.Tables
import db.connection.TableHolder
import org.koin.dsl.bind
import org.koin.dsl.module

val DBModule = module {
    single { Tables }.bind<TableHolder>()
}