package modules

import db.dbModule
import service.serviceModule

val stockModules = listOf(
    dbModule,
    serviceModule
)
