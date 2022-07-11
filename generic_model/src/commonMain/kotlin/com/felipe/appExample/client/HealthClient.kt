package com.felipe.appExample.client

interface HealthClient {
    fun getDate(callback: AppClient.ResponseLambda<String?>): AppClient.Response<String?>?
}