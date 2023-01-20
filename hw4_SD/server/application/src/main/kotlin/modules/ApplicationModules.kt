package modules

import modules.guice.GuiceInjector
import modules.guice.GuiceModules


val applicationModules = listOf(
    DBModule,
    ServicesModule,
    GuiceInjector,
    GuiceModules
)
