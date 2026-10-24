package com.souiid.demoproject.data

import com.souiid.demoproject.domain.model.AppError
import com.souiid.demoproject.domain.model.AppResult
import com.souiid.demoproject.domain.model.User
import com.souiid.demoproject.domain.repository.IUserRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.coroutines.cancellation.CancellationException

class UserRepository: IUserRepository {

    private val firestore = Firebase.firestore
    private val auth: FirebaseAuth = Firebase.auth

    private val _currentUser = MutableStateFlow<User?>(null)
    override val currentUser = _currentUser.asStateFlow()

    override suspend fun fetchUser(userId: String): AppResult<User> {
        return try {
            val snapshot = firestore.collection("users").document(userId).get()

            if (!snapshot.exists) {
                return AppResult.Failure(AppError.Unknown())
            }

            val user = snapshot.data<User>()
            if (user.id == auth.currentUser?.uid) {
                _currentUser.value = user
            }
            AppResult.Success(user)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            AppResult.Failure(AppError.Unknown())
        }
    }
}