package com.souiid.demoproject.di

import com.souiid.demoproject.data.AuthRepository
import com.souiid.demoproject.data.SignUpInfoStore
import com.souiid.demoproject.data.SignUpRepository
import com.souiid.demoproject.domain.repository.IAuthRepository
import com.souiid.demoproject.domain.repository.ISignUpRepository
import com.souiid.demoproject.domain.usecase.auth.ObserveAuthStateUseCase
import com.souiid.demoproject.domain.usecase.auth.SignInUseCase
import com.souiid.demoproject.domain.usecase.auth.SignOutUseCase
import com.souiid.demoproject.domain.usecase.signup.ObserveSignUpInfoUseCase
import com.souiid.demoproject.domain.usecase.signup.UpdateSignUpNameUseCase
import com.souiid.demoproject.domain.usecase.signup.SignUpUserUseCase
import com.souiid.demoproject.domain.usecase.signup.UpdateSignUpDescriptionUseCase
import com.souiid.demoproject.ui.navigation.authgate.AuthGateViewModel
import com.souiid.demoproject.ui.screens.HomeViewModel
import com.souiid.demoproject.ui.screens.auth.signin.SignInViewModel
import com.souiid.demoproject.ui.screens.auth.signup.credential.SignUpCredentialViewModel
import com.souiid.demoproject.ui.screens.auth.signup.description.SignUpDescriptionViewModel
import com.souiid.demoproject.ui.screens.auth.signup.name.SignUpNameViewModel
import org.koin.dsl.module

val appModule = module {

    //REPOSITORY

    single { AuthRepository() }
    single { SignUpRepository(get()) }
    single { SignUpInfoStore() }

    //IREPOSITORY

    single<IAuthRepository> { get<AuthRepository>() }
    single<ISignUpRepository> { get<SignUpRepository>() }

//_________________________________________________________

    //USECASE

    factory { ObserveAuthStateUseCase(get()) }
    factory { ObserveSignUpInfoUseCase(get()) }
    factory { UpdateSignUpNameUseCase(get()) }
    factory { UpdateSignUpDescriptionUseCase(get()) }
    factory { SignUpUserUseCase(get()) }
    factory { SignOutUseCase(get()) }
    factory { SignInUseCase(get()) }
//________________________________________________________

    //VIEWMODEL

    factory { HomeViewModel(get()) }
    factory { AuthGateViewModel(get()) }
    factory { SignUpNameViewModel(get(), get()) }
    factory { SignUpDescriptionViewModel(get(), get()) }
    factory { SignUpCredentialViewModel(get()) }
    factory { SignInViewModel(get()) }

}
