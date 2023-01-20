package visitors

import org.junit.jupiter.api.Test
import tokens.Token

class CalcVisitorTest : TokenVisitorTest<Int>() {
    @Test
    fun `test single number`() = calcTest(listOf(Token.Number(1)), 1)

    @Test
    fun `test all operations`() {
        val tokens = listOf(Token.Number(12), Token.Number(3))

        calcTest(tokens + Token.Operation.Plus, 15)
        calcTest(tokens + Token.Operation.Minus, 9)
        calcTest(tokens + Token.Operation.Multiply, 36)
        calcTest(tokens + Token.Operation.Divide, 4)
    }

    @Test
    fun `test multiple operations`() = calcTest(
        tokens = listOf(
            Token.Number(12),
            Token.Number(3),
            Token.Operation.Multiply,
            Token.Number(12),
            Token.Number(3),
            Token.Operation.Minus,
            Token.Operation.Divide
        ),
        expected = 4
    )

    private fun calcTest(tokens: List<Token>, expected: Int) = visitorTest<CalcVisitor>(tokens, expected)
}