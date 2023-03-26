package modules

import db.dbModule
import service.stockModule
import service.userModule

val applicationModules = listOf(
    dbModule,
    stockModule,
    userModule,
)