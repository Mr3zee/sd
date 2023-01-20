package visitors

import tokens.Token

class CalcVisitor : TokenVisitor<Int> {
    private val stack = ArrayDeque<Int>()

    override fun visit(numberToken: Token.Number) {
        stack.addLast(numberToken.number)
    }

    override fun visit(brace: Token.Brace) {
        error("Braces are not supported in CalcVisitor")
    }

    override fun visit(operation: Token.Operation) {
        stack.addLast(stack.removeLast().let { operation.apply(stack.removeLast(), it) })
    }

    override fun result(): Int {
        return stack.singleOrNull()
            ?: error("Expected stack to contain only one number, actual: <${stack.joinToString()}>")
    }
}
