package visitors

import tokens.Token

class PrintVisitor : TokenVisitor<String> {
    private val builder = StringBuilder()

    private fun append(getter: () -> Any) {
        builder.append(getter().toString())
        builder.append(' ')
    }

    override fun visit(numberToken: Token.Number) = append {
        numberToken.number
    }

    override fun visit(brace: Token.Brace) = append {
        when (brace) {
            Token.Brace.Left -> '('
            Token.Brace.Right -> ')'
        }
    }

    override fun visit(operation: Token.Operation) = append {
        when (operation) {
            Token.Operation.Plus -> '+'
            Token.Operation.Minus -> '-'
            Token.Operation.Multiply -> '*'
            Token.Operation.Divide -> '/'
        }
    }

    override fun result(): String {
        return builder.toString().trim()
    }
}