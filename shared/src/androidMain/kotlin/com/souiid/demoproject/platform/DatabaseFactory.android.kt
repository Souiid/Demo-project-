package com.souiid.demoproject.platform

import androidx.room.Room
import androidx.room.RoomDatabase
import com.souiid.demoproject.room.data.local.AppDatabase

actual class DatabaseFactory actual constructor() {

    actual fun createDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
        val context = AppContextProvider.context
        val dbFile = context.getDatabasePath("demo_database.db")
        return Room.databaseBuilder<AppDatabase>(
            context = context,
            name = dbFile.absolutePath
        )
    }
}