package common.api

import kotlinx.serialization.Serializable

@Serializable
data class Task(
    val id: Int = -1,
    val name: String,
    val description: String? = null,
    val done: Boolean = false,
    val plannerId: Int,
)
