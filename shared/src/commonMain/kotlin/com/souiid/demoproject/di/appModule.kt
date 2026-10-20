package com.souiid.demoproject.di

import com.souiid.demoproject.data.AuthRepository
import com.souiid.demoproject.data.SignUpRepository
import com.souiid.demoproject.domain.usecase.ObserveAuthStateUseCase
import com.souiid.demoproject.domain.usecase.signup.ObserveSignUpInfoUseCase
import com.souiid.demoproject.domain.usecase.signup.UpdateSignUpNameUseCase
import com.souiid.demoproject.domain.usecase.UpdateUserNameUseCase
import com.souiid.demoproject.ui.navigation.authgate.AuthGateViewModel
import com.souiid.demoproject.ui.screens.HomeViewModel
import com.souiid.demoproject.ui.screens.auth.signup.name.SignUpNameViewModel
import org.koin.dsl.module

val appModule = module {

    //REPOSITORY

    single { AuthRepository() }
    single { SignUpRepository() }
//_________________________________________________________

    //USECASE

    factory { UpdateUserNameUseCase(get()) }
    factory { ObserveAuthStateUseCase(get()) }
    factory { ObserveSignUpInfoUseCase(get()) }
    factory { UpdateSignUpNameUseCase(get()) }
//________________________________________________________

    //VIEWMODEL

    factory { HomeViewModel(get()) }
    factory { AuthGateViewModel(get()) }
    factory { SignUpNameViewModel(get(), get()) }

}
