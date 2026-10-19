package com.souiid.demoproject.di

import com.souiid.demoproject.data.SampleRepository
import com.souiid.demoproject.domain.ObserveUserNameUseCase
import com.souiid.demoproject.domain.UpdateUserNameUseCase
import com.souiid.demoproject.ui.screens.HomeViewModel
import org.koin.dsl.module

val appModule = module {

    //REPOSITORY

    single { SampleRepository() }
//_________________________________________________________

    //USECASE

    factory { UpdateUserNameUseCase(get()) }
    factory { ObserveUserNameUseCase(get()) }
//________________________________________________________

    //VIEWMODEL

    factory { HomeViewModel(get(), get()) }
}
