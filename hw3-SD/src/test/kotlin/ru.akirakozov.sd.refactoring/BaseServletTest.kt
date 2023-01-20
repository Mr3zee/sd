package ru.akirakozov.sd.refactoring

import org.eclipse.jetty.http.HttpMethod
import org.eclipse.jetty.server.Server
import org.junit.jupiter.api.AssertionFailureBuilder
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import ru.akirakozov.sd.refactoring.db.BaseDBProvider
import java.net.HttpURLConnection
import java.sql.Connection
import java.sql.DriverManager


@Execution(ExecutionMode.SAME_THREAD)
open class BaseServletTest {

    protected fun withServer(port: Int = 60000, handler: TestContext.() -> Unit) {
        val provider = initializeDB()
        runServer(provider, port) { server ->
            TestContext(server).apply(handler)
        }
    }

    private fun runServer(provider: BaseDBProvider, port: Int, execute: (Server) -> Unit) {
        provider.use { dbProvider ->
            ProductServer.withServer(dbProvider, port) {
                try {
                    it.start()
                    execute(it)
                } finally {
                    it.ensureStopServer()
                }
            }
        }
    }

    private fun Server.ensureStopServer() {
        stop()
        join()
    }

    private fun initializeDB(): BaseDBProvider {
        val connection = DriverManager.getConnection(DB_URL)
        connection.createTable()
        connection.cleanTable()

        return TestDBProvider(connection)
    }

    @Suppress("SqlWithoutWhere")
    private fun Connection.cleanTable() {
        val sql = """DELETE FROM PRODUCT"""

        createStatement().use { it.executeUpdate(sql) }
    }

    private fun Connection.createTable() {
        val sql = """
            CREATE TABLE IF NOT EXISTS PRODUCT(
                ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                NAME TEXT NOT NULL,  
                PRICE INT NOT NULL
            )
        """.trimIndent()

        createStatement().use { it.executeUpdate(sql) }
    }

    protected data class TestContext(private val server: Server) {
        fun sendRequest(
            path: String,
            method: HttpMethod = HttpMethod.GET,
            requestParameters: MutableMap<String, Any>.() -> Unit = {}
        ): ServerResponse {
            val parameters = buildMap(requestParameters)
                .entries
                .joinToString("&&") { "${it.key}=${it.value}" }
                .takeIf { it.isNotBlank() }
                ?.let { "?$it" }
                ?: ""
            val fullPath = server.uri.resolve("$path$parameters").toURL()

            val connection = fullPath.openConnection() as HttpURLConnection?
                ?: error("Unable to open connection for $path")
            connection.requestMethod = method.name

            val code = connection.responseCode
            val body = when (code) {
                in 200..299 -> connection.body.filter { it !in HtmlBasicSyntax }
                else -> connection.error
            }
            return ServerResponse(code, body).also { connection.disconnect() }
        }

        private val HttpURLConnection.body get() = inputStream.bufferedReader().readLines()
        private val HttpURLConnection.error get() = errorStream.bufferedReader().readLines()
    }

    protected data class ServerResponse(
        val statusCode: Int,
        val body: List<String>
    )

    protected fun assertOK(response: ServerResponse) {
        assertInRange(200..299, response.statusCode) {
            """
                Unexpected status code: ${response.statusCode}, message:
                ${response.body.joinToString("\n")}
            """.trimIndent()
        }
    }

    protected fun <T : Comparable<T>> assertInRange(expectedRange: ClosedRange<T>, actual: T, message: () -> String) {
        if (actual !in expectedRange) {
            AssertionFailureBuilder
                .assertionFailure()
                .message(message())
                .expected("$expectedRange")
                .actual(actual)
                .buildAndThrow()
        }
    }

    companion object {
        private val HtmlBasicSyntax = setOf("<html><body>", "</body></html>")

        private const val DB_URL = "jdbc:sqlite:testing.db"
    }
}

private class TestDBProvider(connection: Connection) : BaseDBProvider(connection)
