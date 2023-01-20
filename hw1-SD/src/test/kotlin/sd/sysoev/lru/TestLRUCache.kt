package sd.sysoev.lru

import kotlin.test.Test
import kotlin.test.assertEquals

class TestLRUCache {
    @Test
    fun `test simple put and get`() = withCache {
        cache.put("key", 1)
        assertEquals(1, cache.get("key"))
        assertEquals(null, cache.get("key_1"))
    }

    @Test
    fun `test three puts and gets`() = withCache {
        cache.put("1", 1)
        cache.put("2", 2)
        cache.put("3", 3)

        assertEquals(1, cache.get("1"))
        assertEquals(2, cache.get("2"))
        assertEquals(3, cache.get("3"))
        assertEquals(null, cache.get("4"))
    }

    @Test
    fun `test cache overflow on put`() = withCache(capacity = 3) {
        cache.put("1", 1)
        cache.put("2", 2)
        cache.put("3", 3)
        cache.put("4", 4)

        assertEquals(null, cache.get("1"))
        assertEquals(2, cache.get("2"))
        assertEquals(3, cache.get("3"))
        assertEquals(4, cache.get("4"))
    }

    @Test
    fun `test cache replace on put`() = withCache(capacity = 3) {
        cache.put("1", 1)
        cache.put("2", 2)
        cache.put("3", 3)
        cache.put("1", 4)
        cache.put("5", 5)

        assertEquals(4, cache.get("1"))
        assertEquals(null, cache.get("2"))
        assertEquals(3, cache.get("3"))
        assertEquals(5, cache.get("5"))
    }

    @Test
    fun `test cache retention`() = withCache(capacity = 3) {
        cache.put("1", 1)
        cache.put("2", 2)
        cache.put("3", 3)
        assertEquals(1, cache.get("1"))

        cache.put("4", 4)

        assertEquals(null, cache.get("2"))
        assertEquals(3, cache.get("3"))
        assertEquals(4, cache.get("4"))
        assertEquals(1, cache.get("1"))
    }

    private fun withCache(capacity: Int = 10, body: LRUCacheHolder<Int>.() -> Unit) {
        LRUCacheHolder(LRUCache<Int>(capacity)).apply(body)
    }

    private data class LRUCacheHolder<T>(
        val cache: LRUCache<T>
    )
}
