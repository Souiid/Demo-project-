package com.souiid.demoproject.di

import com.souiid.demoproject.ktor.data.remote.MealApi
import com.souiid.demoproject.ktor.data.repository.MealRepository
import com.souiid.demoproject.ktor.domain.repository.IMealRepository
import com.souiid.demoproject.ktor.domain.usecase.GetMealCategoriesUseCase
import com.souiid.demoproject.ktor.domain.usecase.SearchMealsUseCase
import com.souiid.demoproject.ui.screens.app.ktor.MealCategoryListViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val ktorModule = module {
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }
    }

    //API
    single { MealApi(get()) }

    //REPOSITORY
    single<IMealRepository> { MealRepository(get()) }

    //USECASE
    factory { SearchMealsUseCase(get()) }
    factory { GetMealCategoriesUseCase(get()) }

    //VIEWMODEL
    factory { MealCategoryListViewModel(get()) }
}