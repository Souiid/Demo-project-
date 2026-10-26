package com.souiid.demoproject.di

import com.souiid.demoproject.data.AuthRepository
import com.souiid.demoproject.data.PostRepository
import com.souiid.demoproject.data.SignUpInfoStore
import com.souiid.demoproject.data.SignUpRepository
import com.souiid.demoproject.data.UserRepository
import com.souiid.demoproject.domain.repository.IAuthRepository
import com.souiid.demoproject.domain.repository.IPostRepository
import com.souiid.demoproject.domain.repository.ISignUpRepository
import com.souiid.demoproject.domain.repository.IUserRepository
import com.souiid.demoproject.domain.usecase.auth.ObserveAuthStateUseCase
import com.souiid.demoproject.domain.usecase.auth.SignInUseCase
import com.souiid.demoproject.domain.usecase.auth.SignOutUseCase
import com.souiid.demoproject.domain.usecase.post.CreatePostUseCase
import com.souiid.demoproject.domain.usecase.post.GetFeedPostsUseCase
import com.souiid.demoproject.domain.usecase.post.GetUserPostsUseCase
import com.souiid.demoproject.domain.usecase.signup.ObserveSignUpInfoUseCase
import com.souiid.demoproject.domain.usecase.signup.UpdateSignUpNameUseCase
import com.souiid.demoproject.domain.usecase.signup.SignUpUserUseCase
import com.souiid.demoproject.domain.usecase.signup.UpdateSignUpDescriptionUseCase
import com.souiid.demoproject.domain.usecase.user.FetchUserUseCase
import com.souiid.demoproject.domain.usecase.user.FollowUserUseCase
import com.souiid.demoproject.domain.usecase.user.GetFollowerUserIdsUseCase
import com.souiid.demoproject.domain.usecase.user.GetFollowingUserIdsUseCase
import com.souiid.demoproject.domain.usecase.user.GetFollowingUsersUseCase
import com.souiid.demoproject.domain.usecase.user.GetProfileStatsUseCase
import com.souiid.demoproject.domain.usecase.user.ObserveCurrentUserUseCase
import com.souiid.demoproject.domain.usecase.user.SearchUsersUseCase
import com.souiid.demoproject.domain.usecase.user.UnfollowUserUseCase
import com.souiid.demoproject.domain.usecase.user.UpdateUserUseCase
import com.souiid.demoproject.domain.usecase.user.UploadPhotoUseCase
import com.souiid.demoproject.ui.navigation.authgate.AuthGateViewModel
import com.souiid.demoproject.ui.screens.app.firebase.editprofile.EditProfileViewModel
import com.souiid.demoproject.ui.screens.app.firebase.feed.FirebaseFeedViewModel
import com.souiid.demoproject.ui.screens.app.firebase.profile.FirebaseProfileViewModel
import com.souiid.demoproject.ui.screens.app.firebase.friends.FriendsViewModel
import com.souiid.demoproject.ui.screens.app.firebase.profile.FirebaseProfileListViewModel
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
    single { UserRepository() }
    single { PostRepository() }

    //IREPOSITORY

    single<IAuthRepository> { get<AuthRepository>() }
    single<ISignUpRepository> { get<SignUpRepository>() }
    single<IUserRepository> { get<UserRepository>() }
    single<IPostRepository> { get<PostRepository>() }

//_________________________________________________________

    //USECASE

    factory { ObserveAuthStateUseCase(get()) }
    factory { ObserveSignUpInfoUseCase(get()) }
    factory { UpdateSignUpNameUseCase(get()) }
    factory { UpdateSignUpDescriptionUseCase(get()) }
    factory { SignUpUserUseCase(get()) }
    factory { SignOutUseCase(get()) }
    factory { SignInUseCase(get()) }
    factory { ObserveCurrentUserUseCase(get()) }
    factory { FetchUserUseCase(get()) }
    factory { UpdateUserUseCase(get()) }
    factory { UploadPhotoUseCase(get()) }
    factory { SearchUsersUseCase(get()) }
    factory { FollowUserUseCase(get()) }
    factory { UnfollowUserUseCase(get()) }
    factory { GetFollowingUserIdsUseCase(get()) }
    factory { GetFollowingUsersUseCase(get()) }
    factory { CreatePostUseCase(get()) }
    factory { GetFeedPostsUseCase(get(), get(), get()) }
    factory { GetFollowerUserIdsUseCase(get()) }
    factory { GetProfileStatsUseCase(get(), get(), get()) }
    factory { GetUserPostsUseCase(get()) }


//________________________________________________________

    //VIEWMODEL

    factory { FirebaseFeedViewModel(get(), get(), get(), get()) }
    factory { AuthGateViewModel(get()) }
    factory { SignUpNameViewModel(get(), get()) }
    factory { SignUpDescriptionViewModel(get(), get()) }
    factory { SignUpCredentialViewModel(get()) }
    factory { SignInViewModel(get()) }
    factory { FirebaseProfileViewModel(get(), get(), get(), get(), get(), get(), get()) }
    factory { EditProfileViewModel(get(), get(), get()) }
    factory { FriendsViewModel(get(), get(), get(), get(), get(), get()) }
    factory { FirebaseProfileListViewModel(get(), get(), get(), get()) }
}
