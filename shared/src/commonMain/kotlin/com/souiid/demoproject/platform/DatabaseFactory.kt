package com.souiid.demoproject.platform

import androidx.room.RoomDatabase
import com.souiid.demoproject.room.data.local.AppDatabase

expect class DatabaseFactory() {
    fun createDatabaseBuilder(): RoomDatabase.Builder<AppDatabase>
}