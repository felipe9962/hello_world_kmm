package com.felipe.appExample.client

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class AppClientImpl(
    private val server: String,
    private val headerDevice: Map<String, String>
) : AppClient {

    private val scope = MainScope()
    private val serializer = Json {
        ignoreUnknownKeys = true
    }
    private val httpClient = HttpClient {
        defaultRequest {
            headers {
                for (header in headerDevice)
                    header(header.key, header.value)
            }
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 10000
            connectTimeoutMillis = 10000
        }
    }

    override fun updateUser(
        id: Int,
        name: String?,
        birthdate: String?,
        callback: AppClient.ResponseLambda<String?>?
    ): AppClient.Response<String?>? {
        var res: AppClient.Response<String?>? = null

        val scp = scope.launch {
            val response = privateSendRequest<String?>(
                Endpoints.USER,
                HttpMethod.Post,
                serializer.encodeToString(Structures.User(id, name, birthdate)),
                mapOf()
            )
            callback?.onResponse(response)
            res = response
        }

        if (callback == null)
            runBlocking { scp.join() }

        return res
    }

    override fun uploadUser(
        name: String?,
        birthdate: String?,
        callback: AppClient.ResponseLambda<String?>?
    ): AppClient.Response<String?>? {
        var res: AppClient.Response<String?>? = null

        val scp = scope.launch {
            val response = privateSendRequest<String?>(
                Endpoints.USER,
                HttpMethod.Put,
                serializer.encodeToString(
                    Structures.User(
                        id = null,
                        name = name,
                        birthdate = birthdate
                    )
                ),
                mapOf()
            )
            callback?.onResponse(response)
            res = response
        }

        if (callback == null)
            runBlocking { scp.join() }

        return res
    }

    override fun deleteUser(
        id: Int,
        callback: AppClient.ResponseLambda<String?>?
    ): AppClient.Response<String?>? {
        var res: AppClient.Response<String?>? = null

        val scp = scope.launch {
            val response = privateSendRequest<String?>(
                Endpoints.USER + "/$id",
                HttpMethod.Delete,
                null,
                mapOf()
            )
            callback?.onResponse(response)
            res = response
        }

        if (callback == null)
            runBlocking { scp.join() }

        return res
    }

    override fun getAllUsers(callback: AppClient.ResponseLambda<Array<Structures.User>>?): AppClient.Response<Array<Structures.User>>? {
        var res: AppClient.Response<Array<Structures.User>>? = null

        val scp = scope.launch {
            val response = privateSendRequest<Array<Structures.User>>(
                Endpoints.USER,
                HttpMethod.Get,
                null,
                mapOf()
            )
            callback?.onResponse(response)
            res = response
        }

        if (callback == null)
            runBlocking { scp.join() }

        return res
    }

    override fun getUser(
        id: Int,
        callback: AppClient.ResponseLambda<Structures.User?>?
    ): AppClient.Response<Structures.User?>? {
        var res: AppClient.Response<Structures.User?>? = null

        val scp = scope.launch {
            val response = privateSendRequest<Structures.User?>(
                Endpoints.USER, HttpMethod.Get, Any(), mapOf(
                    "id" to id.toString()
                )
            )
            callback?.onResponse(response)
            res = response
        }

        if (callback == null)
            runBlocking { scp.join() }

        return res
    }

    private suspend inline fun <reified T> privateSendRequest(
        endpoint: String,
        _method: HttpMethod,
        _body: Any?,
        _params: Map<String, String>
    ): AppClient.Response<T> {
        return try {
            val request = httpClient.request {
                url(server + endpoint)
                contentType(ContentType.Application.Json)

                for (_param in _params)
                    parameter(_param.key, _param.value)

                setBody(_body)
                method = _method
            }

            if (request.status.value == 200) {
                if (request.contentType() == null)
                    return AppClient.Response(null, null)
                val body = request.bodyAsText()
                return AppClient.Response(serializer.decodeFromString<T>(body), null)
            } else
                AppClient.Response(
                    null,
                    AppClient.Error(request.status.value, request.bodyAsText())
                )
        } catch (e: Exception) {
            parseError(e)
        }
    }


    private fun <T> parseError(ex: Exception): AppClient.Response<T> {
        return AppClient.Response(
            null,
            if (ex is ClientRequestException)
                AppClient.Error(ex.response.status.value, ex.message)
            else
                AppClient.Error(-1, ex.message)
        )
    }

    override fun getDate(callback: AppClient.ResponseLambda<String?>): AppClient.Response<String?>? {
        TODO("Not yet implemented")
    }
}