package com.felipe.appExample.db

import android.content.Context
import com.felipe.db.AndroidExampleDatabase
import com.squareup.sqldelight.android.AndroidSqliteDriver

lateinit var appContext: Context

internal actual fun cache(): AndroidExampleDatabase {
    val driver = AndroidSqliteDriver(AndroidExampleDatabase.Schema, appContext, "android_example.db")
    return AndroidExampleDatabase(driver)
}