package com.felipe.appExample.client

interface AppClient: HealthClient, UserClient {
    data class Error constructor(
        val code: Int,
        val message: String?
    )

    data class Response<T> constructor(
        val result: T?,
        val error: Error?
    )

    fun interface ResponseLambda<T> {
        fun onResponse(response: Response<T>)
    }
}