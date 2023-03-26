import db.UserTables
import db.connection.PostgresDbConnection
import db.connection.TableHolder
import db.util.tx
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.deleteAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.core.component.inject
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.junit5.KoinTestExtension
import org.koin.test.mock.declare
import org.opentest4j.AssertionFailedError
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.InternetProtocol
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import service.ApiStockService
import service.StockService
import service.UserService
import service.UserServiceImpl

@Testcontainers
class TestUserService : KoinTest {
    @Container
    val postgres: PostgresContainerWithFixedPort<*> = PostgresContainerWithFixedPort("postgres:15.1")
        .withFixedExposedPort(5678, 5432)
        .withPassword("postgres")
        .withUsername("postgres")
        .withDatabaseName("test")

    @Container
    val market: GenericContainer<*> = GenericContainer("sd-stock-service:latest")
        .withExposedPorts(8080)
        .withEnv("KTOR_APPLICATION_HOST", "0.0.0.0")
        .withEnv("DB_POSTGRES_HOST", "host.docker.internal")
        .withEnv("DB_POSTGRES_PORT", "5678")
        .withEnv("DB_POSTGRES_NAME", "test")
        .dependsOn(postgres)
        .waitingFor(Wait.forHttp("/health").forStatusCode(200))


    private val userService by inject<UserService>()

    @Suppress("unused")
    @RegisterExtension
    @JvmField
    val koinTestExtension = KoinTestExtension.create {
        modules(
            module {
                single<UserService> { UserServiceImpl() }
                single { UserTables }.bind<TableHolder>()
            }
        )
    }

    private lateinit var connection: PostgresDbConnection

    @BeforeEach
    fun setup() {
        runBlocking {
            connection = PostgresDbConnection(
                dbUsername = "postgres",
                dbPassword = "postgres",
                host = postgres.host,
                port = 5678,
                name = "test"
            ).apply { init(isDebug = true) }
        }

        declare<StockService> {
            ApiStockService(market.host, market.firstMappedPort)
        }
    }

    @AfterEach
    fun teardown() {
        connection.close()
    }

    @Test
    fun `test the world`() = withTestData {
        val stocks = userService.getAvailableStocks().sortedBy { it.code }.toTypedArray()

        val expectedStokes = arrayOf(
            StockInfo("APPL", 160.2, 1000),
            StockInfo("GOOGL", 102.77, 2000),
            StockInfo("JB", 834.1, 10000),
            StockInfo("NFLX", 328.0, 3000),
        )

        Assertions.assertArrayEquals(expectedStokes, stocks)

        val user = userService.register()

        Assertions.assertEquals(1, user)

        val initialBalance = userService.getAccountInfo(user)

        Assertions.assertNotNull(initialBalance)

        Assertions.assertEquals(0.0, initialBalance!!.balance)
        Assertions.assertEquals(0.0, initialBalance.stockValue)

        val initialPortfolio = userService.getPortfolio(user)

        Assertions.assertEquals(0, initialPortfolio.stocks.size)

        val deal1 = userService.buyStock(user, "APPL", 100)

        Assertions.assertTrue { deal1 is DealResult.Failure }

        userService.deposit(user, 10000.0)

        val balance1 = userService.getAccountInfo(user)!!

        Assertions.assertEquals(10000.0, balance1.balance)
        Assertions.assertEquals(0.0, balance1.stockValue)

        val deal2 = userService.buyStock(user, "APPL", 10001)

        Assertions.assertTrue { deal2 is DealResult.Failure }

        val deal3 = userService.buyStock(user, "APPL", 50)

        Assertions.assertTrue { deal3 is DealResult.Success }

        val balance2 = userService.getAccountInfo(user)!!

        Assertions.assertEquals(1990.000000000001, balance2.balance)
        Assertions.assertEquals(8009.999999999999, balance2.stockValue)

        val stocks1 = userService.getAvailableStocks().sortedBy { it.code }.toTypedArray()

        val expectedStokes1 = arrayOf(
            StockInfo("APPL", 160.2, 950),
            StockInfo("GOOGL", 102.77, 2000),
            StockInfo("JB", 834.1, 10000),
            StockInfo("NFLX", 328.0, 3000),
        )

        Assertions.assertArrayEquals(expectedStokes1, stocks1)

        val portfolio1 = userService.getPortfolio(user)

        Assertions.assertEquals(1, portfolio1.stocks.size)
        Assertions.assertEquals(StockInfo("APPL", 160.2, 50), portfolio1.stocks[0])

        val sell1 = userService.sellStock(user, "NFLX", 10)

        Assertions.assertTrue { sell1 is DealResult.Failure }

        val sell2 = userService.sellStock(user, "APPL", 60)

        Assertions.assertTrue { sell2 is DealResult.Failure }

        val sell3 = userService.sellStock(user, "APPL", 10)

        Assertions.assertTrue { sell3 is DealResult.Success }

        val deal4 = userService.buyStock(user, "JB_", 1)

        Assertions.assertTrue { deal4 is DealResult.Failure }

        val deal5 = userService.buyStock(user, "JB", 1)

        Assertions.assertTrue { deal5 is DealResult.Success }

        val balance3 = userService.getAccountInfo(user)!!

        Assertions.assertEquals(2757.900000000001, balance3.balance)
        Assertions.assertEquals(7242.1, balance3.stockValue)

        val stocks2 = userService.getAvailableStocks().sortedBy { it.code }.toTypedArray()

        val expectedStokes2 = arrayOf(
            StockInfo("APPL", 160.2, 960),
            StockInfo("GOOGL", 102.77, 2000),
            StockInfo("JB", 834.1, 9999),
            StockInfo("NFLX", 328.0, 3000),
        )

        Assertions.assertArrayEquals(expectedStokes2, stocks2)

        val portfolio2 = userService.getPortfolio(user).stocks.sortedBy { it.code }

        Assertions.assertEquals(2, portfolio2.size)
        Assertions.assertEquals(StockInfo("APPL", 160.2, 40), portfolio2[0])
        Assertions.assertEquals(StockInfo("JB", 834.1, 1), portfolio2[1])

    }

    private fun withTestData(body: suspend () -> Unit): Unit = runBlocking {
        try {
            client.delete {
                configureRequest("/admin/update/clear")
            }.apply {
                Assertions.assertEquals(HttpStatusCode.OK, status)
            }

            client.put {
                configureRequest("/admin/add/company")
                parameter("name", "Apple")
                parameter("code", "APPL")
                parameter("value", "160.2")
                parameter("quantity", "1000")
            }.apply {
                Assertions.assertEquals(HttpStatusCode.OK, status)
            }

            client.put {
                configureRequest("/admin/add/company")
                parameter("name", "Google")
                parameter("code", "GOOGL")
                parameter("value", "102.77")
                parameter("quantity", "2000")
            }.apply {
                Assertions.assertEquals(HttpStatusCode.OK, status)
            }

            client.put {
                configureRequest("/admin/add/company")
                parameter("name", "Netflix")
                parameter("code", "NFLX")
                parameter("value", "328.0")
                parameter("quantity", "3000")
            }.apply {
                Assertions.assertEquals(HttpStatusCode.OK, status)
            }

            client.put {
                configureRequest("/admin/add/company")
                parameter("name", "JetBrains")
                parameter("code", "JB")
                parameter("value", "834.1")
                parameter("quantity", "10000")
            }.apply {
                Assertions.assertEquals(HttpStatusCode.OK, status)
            }

            tx {
                UserTables.Portfolio.deleteAll()
                UserTables.User.deleteAll()
            }

            body()
        } catch (e: Throwable) {
            println("------- MARKET LOG START -------")
            println(market.logs)
            println("------- MARKET LOG END -------")
        }
    }

    private fun HttpRequestBuilder.configureRequest(path: String) {
        url {
            host = market.host
            port = market.firstMappedPort
            path(path)
        }

        parameter("id", "admin")
    }

    private val client by lazy {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json()
            }
        }
    }
}

class PostgresContainerWithFixedPort<SELF : PostgresContainerWithFixedPort<SELF>>(imageName: String) :
    PostgreSQLContainer<SELF>(imageName) {

    fun withFixedExposedPort(hostPort: Int, containerPort: Int): SELF {
        super.addFixedExposedPort(hostPort, containerPort, InternetProtocol.TCP)
        return self()
    }
}