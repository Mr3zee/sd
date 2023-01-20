import tokens.Tokenizer
import visitors.CalcVisitor
import visitors.ParseVisitor
import visitors.PrintVisitor

object Calculator {
    fun parseAndEvaluate(input: String): Pair<String, Int> {
        val rawTokens = Tokenizer.parse(input.byteInputStream())

        val postFixTokens = ParseVisitor().apply { visit(rawTokens) }.result()
        val str = PrintVisitor().apply { visit(postFixTokens) }.result()
        val answer = CalcVisitor().apply { visit(postFixTokens) }.result()

        return str to answer
    }
}
