package com.souiid.demoproject.data

import com.souiid.demoproject.domain.repository.IAuthRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.map

class AuthRepository: IAuthRepository {

    private val auth: FirebaseAuth = Firebase.auth
    private val firestore: FirebaseFirestore = Firebase.firestore

    val authState: Flow<FirebaseUser?> = auth.authStateChanged

    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> = _userName

    fun updateUserName(userName: String) {
        _userName.value = userName
    }

    suspend fun signOut() {
        auth.signOut()
    }

    override suspend fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
    }

    fun observeUserProfile(userId: String): Flow<Boolean> {
        return Firebase.firestore.collection("users").document(userId)
            .snapshots
            .map { it.exists }
    }
}