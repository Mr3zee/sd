package visitors

import tokens.Token

class ParseVisitor : TokenVisitor<List<Token>> {
    private val result = mutableListOf<Token>()
    private val operationStack = mutableListOf<Token>()

    override fun visit(numberToken: Token.Number) {
        result.add(numberToken)
    }

    override fun visit(brace: Token.Brace) {
        when (brace) {
            Token.Brace.Left -> operationStack.add(brace)
            Token.Brace.Right -> {
                while (operationStack.isNotEmpty() && operationStack.last() != Token.Brace.Left) {
                    result.add(operationStack.removeLast())
                }
                if (operationStack.isNotEmpty()) {
                    operationStack.removeLast()
                }
            }
        }
    }

    override fun visit(operation: Token.Operation) {
        while (
            operationStack.isNotEmpty() &&
            operationStack.last().let { it is Token.Operation && it.precedence > operation.precedence }
        ) {
            result.add(operationStack.removeLast())
        }
        operationStack.add(operation)
    }

    override fun result(): List<Token> {
        operationStack.reversed().forEach {
            result.add(it)
        }

        return result
    }
}
