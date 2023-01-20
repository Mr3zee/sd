@file:Suppress("unused")

package tokens

import visitors.TokenVisitor

sealed interface Token {
    fun accept(visitor: TokenVisitor<*>)

    sealed interface Brace : Token {
        object Left : Brace
        object Right : Brace

        override fun accept(visitor: TokenVisitor<*>) {
            visitor.visit(this)
        }
    }

    sealed interface Operation : Token {
        val apply: (Int, Int) -> Int
        val precedence: Int

        object Plus : Operation {
            override val apply = { a: Int, b: Int -> a + b }
            override val precedence = 1
        }

        object Minus : Operation {
            override val apply = { a: Int, b: Int -> a - b }
            override val precedence = 1
        }

        object Multiply : Operation {
            override val apply = { a: Int, b: Int -> a * b }
            override val precedence = 2
        }

        object Divide : Operation {
            override val apply = { a: Int, b: Int -> a / b }
            override val precedence = 2
        }

        override fun accept(visitor: TokenVisitor<*>) {
            visitor.visit(this)
        }
    }

    data class Number(val number: Int) : Token {
        override fun accept(visitor: TokenVisitor<*>) {
            visitor.visit(this)
        }
    }
}
