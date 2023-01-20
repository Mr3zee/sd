package tokens

import java.io.InputStream

class Source(private val input: InputStream) {
    private var buffer: Int = input.read()

    fun next(): Char? {
        return char()
    }

    fun take(): Char? {
        return char().also { read() }
    }

    private fun char() = when (buffer) {
        -1 -> null
        else -> Char(buffer)
    }

    private fun read() {
        buffer = input.read()
    }
}

sealed class ParserState(protected val tokens: List<Token>) {
    abstract fun nextState(source: Source): ParserState

    class Start(tokens: List<Token>) : ParserState(tokens) {
        override fun nextState(source: Source): ParserState {
            return when (source.next()) {
                ' ', '\t', '\n', '\r' -> this.also { source.take() }
                '(' -> Start(tokens + Token.Brace.Left).also { source.take() }
                ')' -> Start(tokens + Token.Brace.Right).also { source.take() }
                '+' -> Start(tokens + Token.Operation.Plus).also { source.take() }
                '-' -> Start(tokens + Token.Operation.Minus).also { source.take() }
                '*' -> Start(tokens + Token.Operation.Multiply).also { source.take() }
                '/' -> Start(tokens + Token.Operation.Divide).also { source.take() }
                in '0'..'9' -> Number(source.take()!!, tokens)
                null -> End(tokens)
                else -> Error("Unsupported symbol found: ${source.take()}")
            }
        }
    }

    class Number(private val digits: List<Char>, tokens: List<Token>) : ParserState(tokens) {
        constructor(digit: Char, tokens: List<Token>) : this(listOf(digit), tokens)

        override fun nextState(source: Source): ParserState {
            return when (source.next()) {
                in '0'..'9' -> Number(digits + source.take()!!, tokens)
                else -> Start(tokens + Token.Number(digits.joinToString("").toInt()))
            }
        }
    }

    class End(tokens: List<Token>) : ParserState(tokens) {
        override fun nextState(source: Source) = this

        fun tokens() = tokens
    }

    class Error(val message: String) : ParserState(emptyList()) {
        override fun nextState(source: Source) = this
    }
}

object Tokenizer {
    fun parse(input: InputStream): List<Token> {
        val source = Source(input)

        when (val endState = parse(ParserState.Start(emptyList()), source)) {
            is ParserState.End -> return endState.tokens()
            is ParserState.Error -> error(endState.message)
            else -> error("Unreachable state: $endState")
        }
    }

    private tailrec fun parse(state: ParserState, source: Source): ParserState {
        return when (state) {
            is ParserState.End, is ParserState.Error -> {
                return state
            }

            else -> parse(state.nextState(source), source)
        }
    }
}
