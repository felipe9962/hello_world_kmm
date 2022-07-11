package com.felipe.appExample.db

import com.felipe.db.AndroidExampleDatabase
import com.soywiz.klock.DateTime
import kotlin.test.Test

class DatabaseTest {

    private lateinit var db: AndroidExampleDatabase

    @Test
    fun databaseTest() {
        db = cache()

        insertUsers()
        updateUsers()
        removeUsers()
    }

    private fun removeUsers() {
        val queries = db.database_initializerQueries

        val list = queries.selectAll().executeAsList()

        for (user in list)
            queries.deleteUserByLocalId(user.local_id)
    }

    private fun updateUsers() {
        val queries = db.database_initializerQueries

        val list = queries.selectAll().executeAsList()

        for ((i, user) in list.withIndex()) {
            val name = "pepe_$i"
            queries.updateUserData(name, DateTime.now().unixMillisLong, user.local_id)
        }
    }


    private fun insertUsers() {
        var i = 0

        val queries = db.database_initializerQueries

        while (i != 10000) {
            val name = "user_$i"
            queries.insertUser(name, DateTime.now().unixMillisLong)
            i++
        }
    }
}