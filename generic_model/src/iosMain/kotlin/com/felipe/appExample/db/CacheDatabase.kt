package com.felipe.appExample.db

import com.felipe.db.AndroidExampleDatabase
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver

internal actual fun cache(): AndroidExampleDatabase {
    val driver = NativeSqliteDriver(AndroidExampleDatabase.Schema, "ios_example.db")
    return AndroidExampleDatabase(driver)
}