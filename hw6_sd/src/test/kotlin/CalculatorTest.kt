import kotlin.test.Test
import kotlin.test.assertEquals

class CalculatorTest {
    @Test
    fun `test single number`() = calculatorTest(
        input = "1",
        expectedPrint = "1",
        expectedAnswer = 1,
    )

    @Test
    fun `test simple operations`() {
        calculatorTest(
            input = "12 +   4   ",
            expectedPrint = "12 4 +",
            expectedAnswer = 16,
        )

        calculatorTest(
            input = "12 -   4   ",
            expectedPrint = "12 4 -",
            expectedAnswer = 8,
        )

        calculatorTest(
            input = "12 *   4   ",
            expectedPrint = "12 4 *",
            expectedAnswer = 48,
        )

        calculatorTest(
            input = "12 /   4   ",
            expectedPrint = "12 4 /",
            expectedAnswer = 3,
        )
    }

    @Test
    fun `test full`() = calculatorTest(
        input = "(1 + 2) * 3 - (4 - (5 - 39)) / (18 + 5 / 3)",
        expectedPrint = "1 2 + 3 * 4 5 39 - - 18 5 3 / + / -",
        expectedAnswer = 7,
    )

    private fun calculatorTest(
        input: String,
        expectedPrint: String,
        expectedAnswer: Int,
    ) {
        val (print, answer) = Calculator.parseAndEvaluate(input)

        assertEquals(expectedPrint, print)
        assertEquals(expectedAnswer, answer)
    }
}