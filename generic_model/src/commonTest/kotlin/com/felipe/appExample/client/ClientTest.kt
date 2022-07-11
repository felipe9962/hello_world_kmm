package com.felipe.appExample.client

import com.felipe.appExample.client.exceptions.ManageError
import com.felipe.appExample.model.RepositoryImpl
import com.soywiz.klock.DateTime
import io.ktor.util.logging.*
import kotlin.test.Test

class ClientTest {

    private lateinit var appClient: AppClientImpl

    @Test
    fun start() {
        appClient = AppClientImpl(Connections.SERVER_USER, mapOf())

        downloadAllUsers()
        modifyUsers()
        deleteAllUsers()
        createUsers()
    }


    private fun downloadAllUsers(): Array<Structures.User> {
        val response = appClient.getAllUsers(null)!!

        if (response.error != null)
            throw ManageError.manageErrorCode(response.error!!.code, response.error!!.message!!)

        println("DOWNLOAD ALL USERS SUCCESS")
        return response.result!!
    }

    private fun deleteAllUsers() {
        val users = downloadAllUsers()

        for (user in users) {
            val response = appClient.deleteUser(user.id!!.toInt(), null)!!
            if (response.error != null)
                throw ManageError.manageErrorCode(response.error!!.code, response.error!!.message!!)
        }

        println("DELETE ALL USERS SUCCESS")
    }

    private fun modifyUsers() {
        val users = downloadAllUsers()

        for ((i, user) in users.withIndex()) {
            val randomName = "RandomName$i"
            val response = appClient.updateUser(
                user.id!!.toInt(),
                randomName,
                if (i % 2 == 0) null else user.birthdate,
                null
            )!!

            if (response.error != null)
                throw ManageError.manageErrorCode(response.error!!.code, response.error!!.message!!)
        }

        println("MODIFY USERS SUCCESS")
    }

    private fun createUsers() {
        var i = 0
        while (i != 100) {
            val name = "KotlinUser$i"
            val birthdate = DateTime.now().unixMillisLong

            val response = appClient.uploadUser(
                name,
                DateTime.fromUnix(birthdate).format("yyyy-MM-dd'T'HH:mm:ss"),
                null
            )!!

            if (response.error != null)
                throw ManageError.manageErrorCode(response.error!!.code, response.error!!.message!!)

            i++
        }

        println("CREATE USERS SUCCESS")
    }
}