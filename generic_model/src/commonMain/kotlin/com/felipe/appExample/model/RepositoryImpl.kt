package com.felipe.appExample.model

import com.felipe.appExample.client.AppClient
import com.felipe.appExample.client.AppClientImpl
import com.felipe.appExample.client.Connections
import com.felipe.appExample.db.cache
import com.felipe.appExample.ui.UserManager
import com.felipe.db.AndroidExampleDatabase

open class RepositoryImpl: Repository {

    protected lateinit var database: AndroidExampleDatabase
    internal var userManager: UserManager? = null
    private var appClient: AppClient? = null

    override fun initDependencies() {
        database = cache()

        initClients()
    }

    internal fun initClients() {
        appClient = AppClientImpl(Connections.SERVER_USER, mapOf())
    }

    override fun getAppClient(): AppClient? {
        return appClient
    }

    override fun getUserManager(): UserManager? {
        return userManager
    }
}