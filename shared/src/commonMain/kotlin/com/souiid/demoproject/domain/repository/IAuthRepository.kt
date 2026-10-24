package com.souiid.demoproject.domain.repository

import dev.gitlive.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface IAuthRepository {
    val authState: Flow<FirebaseUser?>

    suspend fun signIn(email: String, password: String)
    suspend fun signOut()
    fun observeUserProfile(userId: String): Flow<Boolean>
    val currentAuthUser: FirebaseUser?
}