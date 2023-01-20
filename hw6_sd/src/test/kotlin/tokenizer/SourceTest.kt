package tokenizer

import org.junit.jupiter.api.Test
import tokens.Source
import kotlin.test.assertEquals

class SourceTest {
    @Test
    fun `test next and take`() = withSource("helo") {
        assertEquals('h', it.next())
        assertEquals('h', it.next())
        assertEquals('h', it.next())
        assertEquals('h', it.take())
        assertEquals('e', it.take())
        assertEquals('l', it.next())
        assertEquals('l', it.next())
        assertEquals('l', it.take())
        assertEquals('o', it.take())
        assertEquals(null, it.next())
        assertEquals(null, it.take())
    }

    @Test
    fun `test empty`() = withSource("") {
        assertEquals(null, it.next())
        assertEquals(null, it.take())
    }
}

fun withSource(source: String, body: (Source) -> Unit) {
    val src = Source(source.byteInputStream())

    body(src)
}
