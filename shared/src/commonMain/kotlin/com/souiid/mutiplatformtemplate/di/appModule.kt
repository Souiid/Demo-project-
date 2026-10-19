package com.souiid.mutiplatformtemplate.di

import com.souiid.mutiplatformtemplate.data.SampleRepository
import com.souiid.mutiplatformtemplate.domain.ObserveUserNameUseCase
import com.souiid.mutiplatformtemplate.domain.UpdateUserNameUseCase
import com.souiid.mutiplatformtemplate.ui.screens.HomeViewModel
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
