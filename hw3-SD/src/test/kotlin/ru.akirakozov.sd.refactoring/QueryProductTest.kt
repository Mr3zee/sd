package ru.akirakozov.sd.refactoring

import org.junit.jupiter.api.Assertions.assertLinesMatch
import org.junit.jupiter.api.Test


class QueryProductTest : BaseServletTest() {
    @Test
    fun `test empty sum`() = withServer {
        val response = sendRequest("/query") {
            put("command", "sum")
        }

        assertOK(response)
        assertLinesMatch(listOf("Summary price: ", "0"), response.body)
    }

    @Test
    fun `test sum`() = withServer {
        addSampleProducts()

        val response = sendRequest("/query") {
            put("command", "sum")
        }

        assertOK(response)
        assertLinesMatch(listOf("Summary price: ", "360"), response.body)
    }

    @Test
    fun `test empty count`() = withServer {
        val response = sendRequest("/query") {
            put("command", "count")
        }

        assertOK(response)
        assertLinesMatch(listOf("Number of products: ", "0"), response.body)
    }

    @Test
    fun `test count`() = withServer {
        addSampleProducts()

        val response = sendRequest("/query") {
            put("command", "count")
        }

        assertOK(response)
        assertLinesMatch(listOf("Number of products: ", "3"), response.body)
    }

    @Test
    fun `test empty max`() = withServer {
        val response = sendRequest("/query") {
            put("command", "max")
        }

        assertOK(response)
        assertLinesMatch(listOf("<h1>Product with max price: </h1>"), response.body)
    }

    @Test
    fun `test max`() = withServer {
        addSampleProducts()

        val response = sendRequest("/query") {
            put("command", "max")
        }

        assertOK(response)
        assertLinesMatch(listOf("<h1>Product with max price: </h1>", "phone_1\t200</br>"), response.body)
    }

    @Test
    fun `test empty min`() = withServer {
        val response = sendRequest("/query") {
            put("command", "min")
        }

        assertOK(response)
        assertLinesMatch(listOf("<h1>Product with min price: </h1>"), response.body)
    }

    @Test
    fun `test min`() = withServer {
        addSampleProducts()

        val response = sendRequest("/query") {
            put("command", "min")
        }

        assertOK(response)
        assertLinesMatch(listOf("<h1>Product with min price: </h1>", "phone_3\t10</br>"), response.body)
    }

    private fun TestContext.addSampleProducts() {
        sendRequest("/add-product") {
            put("name", "phone_1")
            put("price", 200)
        }

        sendRequest("/add-product") {
            put("name", "phone_2")
            put("price", 150)
        }

        sendRequest("/add-product") {
            put("name", "phone_3")
            put("price", 10)
        }
    }
}
