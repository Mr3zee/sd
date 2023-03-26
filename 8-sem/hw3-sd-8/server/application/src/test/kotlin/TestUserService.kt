import db.UserTables
import db.connection.PostgresDbConnection
import db.connection.TableHolder
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.core.component.inject
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.junit5.KoinTestExtension
import org.koin.test.mock.declare
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
    fun test() = withTestData {
        val stocks = userService.getAvailableStocks()
    }

    private fun withTestData(body: suspend () -> Unit): Unit = runBlocking {
        body()
    }
}

class PostgresContainerWithFixedPort<SELF : PostgresContainerWithFixedPort<SELF>>(imageName: String) :
    PostgreSQLContainer<SELF>(imageName) {

    fun withFixedExposedPort(hostPort: Int, containerPort: Int): SELF {
        super.addFixedExposedPort(hostPort, containerPort, InternetProtocol.TCP)
        return self()
    }
}