package com.felipe.appExample.ui

import com.felipe.db.AndroidExampleDatabase
import com.felipe.db.Database_initializerQueries
import com.felipe.db.User


typealias UserId = Int

interface UserManager {
    fun getUserByLocalId(id: UserId): User?

    fun getUserByRemoteId(id: UserId): User?

    fun saveUser(user: User)

    fun saveUser(name: String, time: Long)

    fun deleteUser(id: UserId)

    fun invalidateUser(vararg ids: UserId)

    fun getDatabase(): AndroidExampleDatabase

    fun getQueries(): Database_initializerQueries

}