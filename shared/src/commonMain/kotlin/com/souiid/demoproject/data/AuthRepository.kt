package com.souiid.demoproject.data

import com.souiid.demoproject.domain.repository.IAuthRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthRepository : IAuthRepository {

    private val auth: FirebaseAuth = Firebase.auth

    override val authState: Flow<FirebaseUser?> = auth.authStateChanged

    override suspend fun signOut() {
        auth.signOut()
    }

    override suspend fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
    }

    override fun observeUserProfile(userId: String): Flow<Boolean> {
        return Firebase.firestore.collection("users").document(userId)
            .snapshots
            .map { it.exists }
    }
}