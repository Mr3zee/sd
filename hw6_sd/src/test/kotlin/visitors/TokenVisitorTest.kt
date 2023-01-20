package visitors

import tokens.Token
import kotlin.test.assertEquals

abstract class TokenVisitorTest<R> {
    protected inline fun <reified V : TokenVisitor<R>> visitorTest(tokens: List<Token>, expected: R) {
        val visitor = V::class.constructors.first().call()
        visitor.visit(tokens)
        assertion(expected, visitor.result())
    }

    protected open fun assertion(expected: R, actual: R) {
        assertEquals(expected, actual)
    }
}
