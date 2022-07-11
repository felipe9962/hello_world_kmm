package com.felipe.appExample.client.exceptions

object ManageError {

    fun manageErrorCode(code: Int, message: String): Exception {
        if (code > 499)
            return HttpServerException(code, message)

        if (code == 405 || code == 412 || code == 415)
            return RequestException(code, "Check your request, missing parameters or invalid values")

        if (code in 400..499)
            return HttpClientException(code, message)

        return Exception(message)
    }

}