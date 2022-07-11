package com.felipe.appExample.client.exceptions

class HttpServerException(private val code: Int, message: String): Exception(message) {
    fun getCode(): Int {
        return code
    }
}