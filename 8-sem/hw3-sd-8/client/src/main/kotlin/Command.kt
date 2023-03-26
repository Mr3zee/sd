@file:OptIn(ExperimentalCli::class)

import kotlinx.cli.ArgType
import kotlinx.cli.ExperimentalCli
import kotlinx.cli.Subcommand

class ExecutedCommand {
    lateinit var command: StockSubcommand
}

sealed class StockSubcommand(
    name: String,
    description: String,
    private val executedCommand: ExecutedCommand,
) : Subcommand(name, description) {
    final override fun execute() {
        executedCommand.command = this
        onCall()
    }

    open fun onCall() {}
}

class Login(executedCommand: ExecutedCommand) : StockSubcommand("login", "Login into account", executedCommand) {
    val id by argument(ArgType.Int, description = "User id")
}

class Register(executedCommand: ExecutedCommand) : StockSubcommand("register", "Register new user", executedCommand)

class Deposit(executedCommand: ExecutedCommand) : StockSubcommand("deposit", "Deposit money into account", executedCommand) {
    val amount by argument(ArgType.Double, description = "Amount to deposit")
}

class Stocks(executedCommand: ExecutedCommand) : StockSubcommand("stocks", "Display stocks info", executedCommand)

class Balance(executedCommand: ExecutedCommand) : StockSubcommand("balance", "Display account balance", executedCommand)

class PortfolioCommand(executedCommand: ExecutedCommand) : StockSubcommand("portfolio", "Display portfolio", executedCommand)

class Buy(executedCommand: ExecutedCommand) : StockSubcommand("buy", "Buy stocks", executedCommand) {
    val code by argument(ArgType.String, description = "Code of the stock")
    val amount by argument(ArgType.Int, description = "Amount of the selected stock to buy")
}

class Sell(executedCommand: ExecutedCommand) : StockSubcommand("sell", "Sell stocks", executedCommand) {
    val code by argument(ArgType.String, description = "Code of the stock")
    val amount by argument(ArgType.Int, description = "Amount of the selected stock to sell")
}
