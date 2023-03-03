package visitors

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import tokens.Token

class ParseVisitorTest : TokenVisitorTest<List<Token>>() {
    @Test
    fun `test single number`() = parseTest(
        tokens = listOf(Token.Number(1)),
        expected = listOf(Token.Number(1))
    )

    @Test
    fun `test simple operation`() = parseTest(
        tokens = listOf(Token.Number(1), Token.Operation.Plus, Token.Number(3)),
        expected = listOf(Token.Number(1), Token.Number(3), Token.Operation.Plus)
    )

    @Test
    fun `test brackets operation`() = parseTest(
        tokens = listOf(
            Token.Brace.Left,
            Token.Number(1),
            Token.Operation.Plus,
            Token.Number(13),
            Token.Brace.Right,
            Token.Operation.Multiply,
            Token.Number(2)
        ),
        expected = listOf(
            Token.Number(1),
            Token.Number(13),
            Token.Operation.Plus,
            Token.Number(2),
            Token.Operation.Multiply,
        )
    )

    @Test
    fun `test operation precedence`() = parseTest(
        tokens = listOf(
            Token.Number(1),
            Token.Operation.Plus,
            Token.Number(13),
            Token.Operation.Multiply,
            Token.Number(2),
            Token.Operation.Divide,
            Token.Number(4),
        ),
        expected = listOf(
            Token.Number(1),
            Token.Number(13),
            Token.Number(2),
            Token.Operation.Multiply,
            Token.Number(4),
            Token.Operation.Divide,
            Token.Operation.Plus,
        )
    )

    @Test
    fun `test multiple operations`() = parseTest(
        tokens = listOf(
            Token.Brace.Left,
            Token.Brace.Left,
            Token.Number(1),
            Token.Operation.Plus,
            Token.Number(13),
            Token.Brace.Right,
            Token.Operation.Multiply,
            Token.Brace.Left,
            Token.Number(4),
            Token.Operation.Minus,
            Token.Number(-4),
            Token.Brace.Right,
            Token.Brace.Right,
        ),
        expected = listOf(
            Token.Number(1),
            Token.Number(13),
            Token.Operation.Plus,
            Token.Number(4),
            Token.Number(-4),
            Token.Operation.Minus,
            Token.Operation.Multiply,
        )
    )

    private fun parseTest(tokens: List<Token>, expected: List<Token>) =
        visitorTest<ParseVisitor>(tokens, expected)

    override fun assertion(expected: List<Token>, actual: List<Token>) {
        assertTokenListsEquals(expected, actual)
    }
}

fun assertTokenListsEquals(expected: List<Token>, actual: List<Token>) {
    assertEquals(
        expected.size,
        actual.size
    ) { "Sizes of answers differ. Expected: <${expected.joinToString()}>, actual: ${actual.joinToString()}" }
    (expected zip actual).forEach { (expectedToken, actualToken) ->
        when {
            expectedToken is Token.Number && actualToken is Token.Number -> assertEquals(
                expectedToken.number,
                actualToken.number
            )

            else -> assertEquals(expectedToken, actualToken)
        }
    }
}
