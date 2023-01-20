package tokenizer

import tokens.Token
import tokens.Tokenizer
import visitors.assertTokenListsEquals
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class TokenizerTest {
    @Test
    fun `test empty input`() = tokenizerTest(
        input = "",
        expected = emptyList()
    )

    @Test
    fun `test all tokens`() = tokenizerTest(
        input = "+\t123 45+               -*/(                )          ",
        expected = listOf(
            Token.Operation.Plus,
            Token.Number(123),
            Token.Number(45),
            Token.Operation.Plus,
            Token.Operation.Minus,
            Token.Operation.Multiply,
            Token.Operation.Divide,
            Token.Brace.Left,
            Token.Brace.Right,
        )
    )

    @Test
    fun `test unsupported symbol`() = assertTokenizerFails(
        input = "1 + 3,",
        expectedMessage = "Unsupported symbol found: ,"
    )

    private fun tokenizerTest(input: String, expected: List<Token>) {
        val actual = Tokenizer.parse(input.byteInputStream())
        assertTokenListsEquals(expected, actual)
    }

    @Suppress("SameParameterValue")
    private fun assertTokenizerFails(input: String, expectedMessage: String) {
        val e = assertFails {
            Tokenizer.parse(input.byteInputStream())
        }

        assertEquals(expectedMessage, e.message)
    }
}
