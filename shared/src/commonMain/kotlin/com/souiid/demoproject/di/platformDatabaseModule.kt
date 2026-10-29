package com.souiid.demoproject.di

import org.koin.dsl.module
import com.souiid.demoproject.platform.DatabaseFactory

val platformDatabaseModule = module {
    single { DatabaseFactory() }
}