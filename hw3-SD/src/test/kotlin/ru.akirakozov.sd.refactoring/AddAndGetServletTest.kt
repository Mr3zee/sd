package ru.akirakozov.sd.refactoring

import org.junit.jupiter.api.Assertions.assertLinesMatch
import org.junit.jupiter.api.Test


class AddAndGetServletTest : BaseServletTest() {
    @Test
    fun `test empty products`() = withServer {
        val response = sendRequest("/get-products")
        assertOK(response)
        assertLinesMatch(emptyList(), response.body)
    }

    @Test
    fun `test one product`() = withServer {
        val addResponse = sendRequest("/add-product") {
            put("name", "phone")
            put("price", 100)
        }

        assertOK(addResponse)
        assertLinesMatch(listOf("OK"), addResponse.body)

        val getResponse = sendRequest("/get-products")
        assertOK(getResponse)
        assertLinesMatch(listOf("phone\t100</br>"), getResponse.body)
    }

    @Test
    fun `test many products`() = withServer {
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

        val getResponse = sendRequest("/get-products")
        assertOK(getResponse)
        assertLinesMatch(listOf("phone_1\t200</br>", "phone_2\t150</br>", "phone_3\t10</br>",), getResponse.body)
    }
}
