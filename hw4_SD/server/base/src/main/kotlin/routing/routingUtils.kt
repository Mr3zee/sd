package routing

import io.ktor.http.*

fun <T> Parameters.saveReceive(name: String, map: (String) -> T?): T {
    return get(name)?.let(map) ?: throw IllegalArgumentException("Invalid $name parameter")
}

@Suppress("unused")
fun Parameters.saveReceive(name: String): String {
    return saveReceive(name) { it }
}
