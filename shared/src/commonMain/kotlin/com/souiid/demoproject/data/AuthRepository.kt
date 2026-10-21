package com.souiid.demoproject.data

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthRepository {

    private val auth: FirebaseAuth = Firebase.auth
    val authState: Flow<FirebaseUser?> = auth.authStateChanged

    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> = _userName

    fun updateUserName(userName: String) {
        _userName.value = userName
    }

    suspend fun signOut() {
        auth.signOut()
    }

    suspend fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
    }
}