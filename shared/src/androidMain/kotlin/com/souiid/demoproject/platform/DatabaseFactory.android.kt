package com.souiid.demoproject.platform

import androidx.room.Room
import androidx.room.RoomDatabase
import com.souiid.demoproject.room.data.local.AppDatabase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual class DatabaseFactory actual constructor() : KoinComponent {

    private val context: android.content.Context by inject()

    actual fun createDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
        val dbFile = context.getDatabasePath("demo_database.db")
        return Room.databaseBuilder<AppDatabase>(context, dbFile.absolutePath)
    }
}