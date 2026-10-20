package com.souiid.demoproject.di

import com.souiid.demoproject.data.AuthRepository
import com.souiid.demoproject.data.SignUpRepository
import com.souiid.demoproject.domain.ObserveAuthStateUseCase
import com.souiid.demoproject.domain.UpdateUserNameUseCase
import com.souiid.demoproject.ui.navigation.authgate.AuthGateViewModel
import com.souiid.demoproject.ui.screens.HomeViewModel
import org.koin.dsl.module

val appModule = module {

    //REPOSITORY

    single { AuthRepository() }
    single { SignUpRepository() }
//_________________________________________________________

    //USECASE

    factory { UpdateUserNameUseCase(get()) }
    factory { ObserveAuthStateUseCase(get()) }
//________________________________________________________

    //VIEWMODEL

    factory { HomeViewModel(get()) }
    factory { AuthGateViewModel(get()) }

}
