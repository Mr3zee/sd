package visitors

import tokens.Token

interface TokenVisitor<R> {
    fun visit(tokens: List<Token>) {
        tokens.forEach {
            it.accept(this)
        }
    }

    fun visit(numberToken: Token.Number)

    fun visit(brace: Token.Brace)

    fun visit(operation: Token.Operation)

    fun result(): R
}
