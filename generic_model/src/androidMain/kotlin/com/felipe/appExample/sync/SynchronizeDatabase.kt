package com.felipe.appExample.sync

import com.felipe.appExample.client.UserClient
import com.felipe.appExample.client.exceptions.HttpClientException
import com.felipe.appExample.client.exceptions.HttpServerException
import com.felipe.appExample.client.exceptions.RequestException
import com.felipe.appExample.utils.ParserUtils
import com.felipe.db.Database_initializerQueries

object SynchronizeDatabase {

    interface DownloadCallback {
        fun onDownloaded(responseCode: Int)
    }

    const val LIMIT = 100

    fun sync(
        userDatabase: Database_initializerQueries,
        userClient: UserClient,
        downloadCallback: DownloadCallback
    ) {
        val offset = 0L
        var usersNotUploaded = userDatabase.getUsersNotUploaded().executeAsList()

        try {
            //do {
            //if (usersNotUploaded.size < 0)
            //break

            for (u in usersNotUploaded) {
                val response = userClient.uploadUser(
                    u.name,
                    ParserUtils.parseTimeToTimestamp(u.birth_date),
                    null
                )!!

                if (response.error != null) {
                    downloadCallback.onDownloaded(response.error.code)
                    return
                }
            }
/*                    usersNotUploaded =
                        userDatabase.getUsersNotUploaded(offset, LIMIT.toLong()).executeAsList()
                } while (usersNotUploaded.size == LIMIT)*/


            // This should be paginated, but endpoint return it all
            val usersResponse = userClient.getAllUsers(null)!!
            if (usersResponse.error != null) {
                downloadCallback.onDownloaded(usersResponse.error.code)
                return
            }

            val users = usersResponse.result
            if (users == null || users.isEmpty()) {
                downloadCallback.onDownloaded(200)
                return
            }

            for (u in users) {
                val name = u.name ?: ""
                userDatabase.insertUserWithRemoteId(
                    name,
                    ParserUtils.parseTimestampToLong(u.birthdate!!),
                    u.id!!.toLong()
                )
            }

            userDatabase.deleteUsersNotUploaded()
            downloadCallback.onDownloaded(200)
        } catch (e: Exception) {
            val clazz = e.javaClass
            var requestCode = -1
            when (clazz) {
                RequestException::class.java.javaClass -> {
                    requestCode = (e as RequestException).getCode()
                }
                HttpClientException::class.java.javaClass -> {
                    requestCode = (e as HttpClientException).getCode()
                }
                HttpServerException::class.java.javaClass -> {
                    requestCode = (e as HttpServerException).getCode()
                }
            }
            downloadCallback.onDownloaded(requestCode)
        }
    }
}