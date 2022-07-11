package com.felipe.appExample.model

import com.felipe.appExample.client.AppClient
import com.felipe.appExample.ui.UserManager
import com.felipe.db.AndroidExampleDatabase

interface Repository {
    fun initDependencies()

    fun getAppClient(): AppClient?

    fun getUserManager(): UserManager?
}