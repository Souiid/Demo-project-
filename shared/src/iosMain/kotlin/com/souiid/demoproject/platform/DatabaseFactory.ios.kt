package com.souiid.demoproject.platform

import androidx.room.Room
import androidx.room.RoomDatabase
import com.souiid.demoproject.room.data.local.AppDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

actual class DatabaseFactory actual constructor() {

    @OptIn(ExperimentalForeignApi::class)
    actual fun createDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
        val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null
        )?.path ?: error("Unable to find documents directory")

        val dbFile = "$documentDirectory/demo_database.db"
        return Room.databaseBuilder<AppDatabase>(name = dbFile)
    }
}