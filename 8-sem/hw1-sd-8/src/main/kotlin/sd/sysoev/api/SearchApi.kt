package sd.sysoev.api

interface SearchApi {
    suspend fun search(query: String, limit: Int = 5): List<String>
}