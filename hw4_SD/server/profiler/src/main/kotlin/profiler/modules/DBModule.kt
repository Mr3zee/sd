package profiler.modules

import db.connection.TableHolder
import org.koin.dsl.bind
import org.koin.dsl.module
import profiler.db.Tables

val DBModule = module {
    single { Tables }.bind<TableHolder>()
}
