package common.api

import kotlinx.serialization.Serializable

@Serializable
data class Planner(
    val id: Int = -1,
    val name: String,
    val description: String? = null,
    val archived: Boolean = false,
    val tasks: List<Task> = emptyList(),
)
