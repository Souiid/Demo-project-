package com.souiid.demoproject.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.souiid.demoproject.platform.DatabaseFactory
import com.souiid.demoproject.room.data.local.AppDatabase
import com.souiid.demoproject.room.data.repository.FavoriteMealRepository
import com.souiid.demoproject.room.domain.repository.IFavoriteMealRepository
import com.souiid.demoproject.room.domain.usecase.AddFavoriteMealUseCase
import com.souiid.demoproject.room.domain.usecase.ObserveFavoriteMealsUseCase
import com.souiid.demoproject.room.domain.usecase.ObserveIsFavoriteUseCase
import com.souiid.demoproject.room.domain.usecase.RemoveFavoriteMealUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.dsl.module

val databaseModule = module {
    single {
        get<DatabaseFactory>().createDatabaseBuilder()
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }

    single { get<AppDatabase>().favoriteMealDao() }

    single<IFavoriteMealRepository> { FavoriteMealRepository(get()) }

    factory { ObserveFavoriteMealsUseCase(get()) }
    factory { ObserveIsFavoriteUseCase(get()) }
    factory { AddFavoriteMealUseCase(get()) }
    factory { RemoveFavoriteMealUseCase(get()) }

}
