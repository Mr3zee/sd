package db.connection

import io.ktor.server.application.*
import kotlinx.coroutines.runBlocking
import property


fun Application.postgres(isDebug: Boolean = false) = runBlocking {
    postgresApplicationDbConnection(this@postgres).init(isDebug)
}

class PostgresDbConnection(
    override val dbUsername: String,
    override val dbPassword: String,
    host: String,
    port: Int,
    name: String,
) : DbConnection() {
    override val dbUrl: String = "jdbc:postgresql://$host:$port/$name"
    override val driverUrl = "org.postgresql.Driver"
}

fun postgresApplicationDbConnection(application: Application): DbConnection {
    val host: String = application.property("db.postgres.host").getString()
    val port: Int = application.property("db.postgres.port").getString().toInt()
    val name: String = application.property("db.postgres.name").getString()

    val dbUsername = application.property("db.postgres.username").getString()
    val dbPassword = application.property("db.postgres.password").getString()

    return PostgresDbConnection(dbUsername, dbPassword, host, port, name)
}
