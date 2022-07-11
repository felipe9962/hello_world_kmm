package com.felipe.appExample

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

class Greeting {

    fun greeting(): String {
        return "Hello, ${Platform().platform}!"
    }
}