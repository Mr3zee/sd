package sysoev.sd.api.test

import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.junit5.KoinTestExtension
import sysoev.sd.api.credentials.AppProperties

abstract class BaseTwitterTest : KoinTest {
    @Suppress("unused")
    @JvmField
    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create {
        val testModule = module {
            single { AppProperties("") }
        }

        modules(testModule)
    }
}