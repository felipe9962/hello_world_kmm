package com.felipe.appExample.client.exceptions

class HttpClientException(private val code: Int, message: String): Exception(message) {
    fun getCode(): Int {
        return code
    }
}