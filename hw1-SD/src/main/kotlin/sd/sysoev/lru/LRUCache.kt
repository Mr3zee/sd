package sd.sysoev.lru

class LRUCache<T>(private val capacity: Int) {
    private val map = mutableMapOf<String, Node.Value<T>>()
    private val head = Node.Empty<T>()
    private val tail = Node.Empty<T>()

    init {
        require(capacity > 0) {
            "Capacity should be greater than 0"
        }

        head.next = tail
        tail.prev = head
    }

    fun get(key: String): T? {
        return map[key]?.also {
            erase(it)
            addNew(it)

            checkNew(it)
            checkSize()
        }?.value
    }

    fun put(key: String, value: T) {
        val old = map[key]

        val obsolete = when {
            old != null -> old
            map.size == capacity -> when (val node = tail.prev) {
                is Node.Value -> node
                else -> null
            }
            else -> null
        }

        erase(obsolete)

        val new = Node.Value(key, value)
        addNew(new)

        checkNew(new)
        checkSize()
        checkDeletedObsolete(obsolete, new)
    }

    private fun addNew(node: Node.Value<T>) {
        node.next = head.next
        head.next?.prev = node
        node.prev = head
        head.next = node

        map[node.key] = node
    }

    private fun erase(node: Node.Value<T>?) {
        node ?: return

        node.next?.prev = node.prev
        node.prev?.next = node.next

        node.next = null
        node.prev = null

        map.remove(node.key)
    }

    private fun checkDeletedObsolete(obsolete: Node.Value<T>?, new: Node.Value<T>) {
        obsolete ?: return

        assert(obsolete.key == new.key || map[obsolete.key] == null) {
            "Expected least used element to be deleted from map"
        }
    }

    private fun checkSize() {
        assert(map.size <= capacity) {
            "Expected to have map.size <= capacity"
        }
    }

    private fun checkNew(new: Node<T>) {
        assert(head.next == new) {
            "Expected to have new node at the start"
        }
        assert(head == new.prev) {
            "Expected new node to have head as prev"
        }
    }

    private sealed class Node<T>(
        var prev: Node<T>? = null,
        var next: Node<T>? = null,
    ) {
        class Empty<T> : Node<T>()

        class Value<T>(val key: String, val value: T, prev: Node<T>? = null, next: Node<T>? = null): Node<T>(prev, next)
    }
}
