package com.felipe.appExample.db

import co.touchlab.sqliter.DatabaseConfiguration
import com.felipe.db.AndroidExampleDatabase
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver
import com.squareup.sqldelight.drivers.native.wrapConnection

/*
internal actual fun testDbConnection(): SqlDriver {
    val schema = AndroidExampleDatabase.Schema
    return NativeSqliteDriver(
        DatabaseConfiguration(
            name = "android_example_db",
            version = schema.version,
            create = { connection ->
                wrapConnection(connection) { schema.create(it) }
            },
            upgrade = { connection, oldVersion, newVersion ->
                wrapConnection(connection) { schema.migrate(it, oldVersion, newVersion) }
            },
            inMemory = true
        )
    )
}*/
