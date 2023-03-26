import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.cli.ArgParser
import kotlinx.cli.ExperimentalCli
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalCli::class)
fun main() = runBlocking {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }

        defaultRequest {
            url {
                host = "localhost"
                port = 8080
            }
        }
    }

    var currentId: Int? = null

    while (true) {
        try {
            print("stock exchange${(currentId?.let { " {$it}" } ?: "")} $ ")
            val userInput = readln().split(" ").filter { it.isNotEmpty() }
            val parser = ArgParser("stock-exchange")
            hackParser(parser)

            val executedCommand = ExecutedCommand()

            val login = Login(executedCommand)
            val register = Register(executedCommand)
            val deposit = Deposit(executedCommand)
            val balance = Balance(executedCommand)
            val stocks = Stocks(executedCommand)
            val portfolio = PortfolioCommand(executedCommand)
            val buy = Buy(executedCommand)
            val sell = Sell(executedCommand)
            val exit = Exit(executedCommand)
            val logout = Logout(executedCommand)

            parser.subcommands(login, register, deposit, balance, stocks, portfolio, buy, sell, exit, logout)

            parser.parse(userInput.toTypedArray())

            when (executedCommand.command) {
                is Login -> {
                    client.get("/api/login") {
                        parameter("id", login.id)
                    }.run {
                        if (status == HttpStatusCode.OK) {
                            currentId = login.id

                            println("Logged in as $currentId")
                        } else {
                            println("Failed to log in as ${login.id}")
                        }
                    }
                }
                is Logout -> currentId = null
                is Exit -> {
                    break
                }
                is Register -> {
                    val id = client.post("/api/register").body<Int>()
                    currentId = id
                    println("Registered as $id")
                }
                is Deposit -> {
                    currentId ?: error("Not authenticated")

                    client.put("/api/deposit") {
                        parameter("id", currentId)
                        parameter("amount", deposit.amount)
                    }
                }
                is Balance -> {
                    currentId ?: error("Not authenticated")

                    client.get("/api/account-info") {
                        parameter("id", currentId)
                    }.run {
                        val info = if (status == HttpStatusCode.OK) body<AccountInfo>() else {
                            error("Error processing operation")
                        }

                        println("Balance: ${info.balance}")
                        println("Stocks value: ${info.stockValue}")
                    }
                }
                is PortfolioCommand -> {
                    client.get("/api/portfolio") {
                        parameter("id", currentId)
                    }.body<Portfolio>().let {
                        println("Portfolio:")
                        it.stocks.forEach { stock ->
                            println("${stock.code} --- ${stock.stockValue}, owned: ${stock.stockQuantity}")
                        }
                    }
                }
                is Stocks -> {
                    client.get("/api/stocks").body<List<StockInfo>>().let {
                        println("Stock market:")
                        it.forEach { stock ->
                            println("${stock.code} --- ${stock.stockValue}, available: ${stock.stockQuantity}")
                        }
                    }
                }
                is Buy -> {
                    currentId ?: error("Not authenticated")

                    client.put("/api/buy") {
                        parameter("id", currentId)
                        parameter("code", buy.code)
                        parameter("amount", buy.amount)
                    }.body<DealResult>().let {
                        when (it) {
                            is DealResult.Success -> println("Success")
                            is DealResult.Failure -> println("Failure: ${it.reason}")
                        }
                    }
                }
                is Sell -> {
                    currentId ?: error("Not authenticated")

                    client.delete("/api/sell") {
                        parameter("id", currentId)
                        parameter("code", sell.code)
                        parameter("amount", sell.amount)
                    }.body<DealResult>().let {
                        when (it) {
                            is DealResult.Success -> println("Success")
                            is DealResult.Failure -> println("Failure: ${it.reason}")
                        }
                    }
                }
            }
        } catch (e: ParsingException) {
            // ignore
        } catch (e : Exception) {
            println(e.message)
        }
    }
}

class ParsingException : RuntimeException()

private fun hackParser(parser: ArgParser) {
    val field = parser::class.java.getDeclaredField("outputAndTerminate")
    field.isAccessible = true

    val outputAndTerminate: (String, Int) -> Nothing = { message: String, _: Int ->
        println(message)
        throw ParsingException()
    }

    field.set(parser, outputAndTerminate)
}
