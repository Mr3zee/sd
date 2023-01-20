package visitors

import org.junit.jupiter.api.Test
import tokens.Token

class PrintVisitorTest : TokenVisitorTest<String>() {
    @Test
    fun `all print test`() = printTest(
        tokens = listOf(
            Token.Brace.Left,
            Token.Number(12),
            Token.Operation.Minus,
            Token.Number(11),
            Token.Brace.Right,
            Token.Operation.Multiply,
            Token.Number(3),
            Token.Operation.Divide,
            Token.Number(1289),
            Token.Operation.Plus,
            Token.Number(129),
        ),
        expected = "( 12 - 11 ) * 3 / 1289 + 129"
    )

    @Test
    fun `test empty`() = printTest(
        tokens = emptyList(),
        expected = ""
    )

    private fun printTest(tokens: List<Token>, expected: String) = visitorTest<PrintVisitor>(tokens, expected)
}