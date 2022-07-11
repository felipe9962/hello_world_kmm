package com.felipe.appExample.ui

import com.felipe.db.AndroidExampleDatabase
import com.felipe.db.Database_initializerQueries
import com.felipe.db.User

class UserManagerImpl(private val userDatabase: AndroidExampleDatabase): UserManager {

    val OFFSET: Long = 100
    val MAX_SIZE = 200

    private val mUserCache: MutableMap<Int, User> = object : LinkedHashMap<Int, User>(
        0,
        0.75f,
        true
    ) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<Int, User>): Boolean {
            return size > MAX_SIZE
        }
    }

    private val queries = userDatabase.database_initializerQueries

    init {
        loadFirstUsers()
    }

    private fun loadFirstUsers() {
        val users = queries.selectAllPaged()
        users.executeAsList().forEachIndexed { index, user ->
            mUserCache[index] = user
        }
    }

    override fun getUserByLocalId(id: UserId): User {
        var user = mUserCache[id]
        if (user == null) {
            user = queries.getUserByLocalId(id.toLong()).executeAsOne()
            mUserCache[id] = user
        }

        return user
    }

    override fun getUserByRemoteId(id: UserId): User {
        val u = queries.getUserByRemoteId(id.toLong()).executeAsOne()
        mUserCache[u.local_id.toInt()] = u
        return u
    }

    override fun saveUser(user: User) {
        if (user.remote_id != -1L)
            queries.insertUserWithRemoteId(user.name, user.birth_date, user.remote_id)
        else
            queries.insertUser(user.name, user.birth_date)

        mUserCache[user.local_id.toInt()] = user
    }

    override fun saveUser(name: String, time: Long) {
        queries.insertUser(name, time)
    }

    override fun deleteUser(id: UserId) {
        queries.deleteUserByLocalId(id.toLong())
    }

    override fun invalidateUser(vararg ids: UserId) {
        for (id in ids)
            mUserCache.remove(id)
    }

    override fun getDatabase(): AndroidExampleDatabase {
        return userDatabase
    }

    override fun getQueries(): Database_initializerQueries {
        return queries
    }
}