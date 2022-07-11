package com.felipe.appExample

import android.content.Context
import com.felipe.appExample.db.appContext
import com.felipe.appExample.db.cache
import com.felipe.appExample.model.RepositoryImpl
import com.felipe.appExample.ui.UserManagerImpl

class AndroidRepositoryImpl(private val context: Context): RepositoryImpl() {

    override fun initDependencies() {
        appContext = context
        database = cache()
        initClients()
        initManagers()
    }

    private fun initManagers() {
        userManager = UserManagerImpl(database)
    }
}