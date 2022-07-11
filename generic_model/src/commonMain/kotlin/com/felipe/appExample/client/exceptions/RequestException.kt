package com.felipe.appExample.client.exceptions


class RequestException(private val code: Int, message: String) : Exception(message) {
    fun getCode(): Int {
        return code
    }
}