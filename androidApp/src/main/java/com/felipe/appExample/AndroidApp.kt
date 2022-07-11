package com.felipe.appExample

import android.app.Application
import com.felipe.appExample.model.Repository

class AndroidApp : Application() {

    companion object {
        lateinit var instance: AndroidApp
    }

    override fun onCreate() {
        super.onCreate()

        instance = this
        initializeRepository()
    }

    fun initializeRepository() {
        // There can be FirebaseAuth etc.

        repository = AndroidRepositoryImpl(applicationContext)
        repository.initDependencies()
    }

    private lateinit var repository: Repository

    fun getRepository(): Repository {
        return repository
    }

}