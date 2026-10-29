package com.souiid.demoproject.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.souiid.demoproject.platform.DatabaseFactory
import com.souiid.demoproject.room.data.local.AppDatabase
import kotlinx.coroutines.IO
import org.koin.dsl.module

val databaseModule = module {
    single<AppDatabase> {
        get<DatabaseFactory>().createDatabaseBuilder()
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(kotlinx.coroutines.Dispatchers.IO)
            .build()
    }

    single { get<AppDatabase>().favoriteMealDao() }
}

