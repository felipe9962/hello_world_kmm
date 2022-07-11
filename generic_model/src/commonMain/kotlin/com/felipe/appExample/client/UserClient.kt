package com.felipe.appExample.client

interface UserClient {
    fun getUser(
        id: Int,
        callback: AppClient.ResponseLambda<Structures.User?>?
    ): AppClient.Response<Structures.User?>?

    fun getAllUsers(callback: AppClient.ResponseLambda<Array<Structures.User>>?): AppClient.Response<Array<Structures.User>>?
    fun updateUser(
        id: Int,
        name: String?,
        birthdate: String?,
        callback: AppClient.ResponseLambda<String?>?
    ): AppClient.Response<String?>?

    fun deleteUser(
        id: Int,
        callback: AppClient.ResponseLambda<String?>?
    ): AppClient.Response<String?>?

    fun uploadUser(
        name: String?,
        birthdate: String?,
        callback: AppClient.ResponseLambda<String?>?
    ): AppClient.Response<String?>?
}