package com.souiid.demoproject.data

import com.souiid.demoproject.domain.model.AppError
import com.souiid.demoproject.domain.model.AppResult
import com.souiid.demoproject.domain.model.User
import com.souiid.demoproject.domain.repository.IUserRepository
import com.souiid.demoproject.platform.createFileFromUri
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.storage.storage
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

    override suspend fun updateUser(
        firstName: String,
        lastName: String,
        description: String,
        photoUrl: String?
    ): AppResult<Unit> {
        return try {
            val user = _currentUser.value ?: return AppResult.Failure(AppError.Unknown())

            val updates = mutableMapOf<String, Any>(
                "firstName" to firstName,
                "lastName" to lastName,
                "description" to description
            )
            if (photoUrl != null) updates["photoUrl"] = photoUrl

            firestore.collection("users").document(user.id).update(updates)

            _currentUser.value = user.copy(
                firstName = firstName,
                lastName = lastName,
                description = description,
                photoUrl = photoUrl ?: user.photoUrl
            )

            AppResult.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            AppResult.Failure(AppError.Unknown())
        }
    }

    override suspend fun uploadPhoto(uri: String): AppResult<String> {
        val userId = Firebase.auth.currentUser?.uid
            ?: return AppResult.Failure(AppError.Unknown())

        return try {
            val reference = Firebase.storage.reference.child("users/$userId/profile.jpg")
            reference.putFile(createFileFromUri(uri))
            AppResult.Success(reference.getDownloadUrl())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            AppResult.Failure(AppError.Unknown())
        }
    }

    override suspend fun searchUsers(query: String): AppResult<List<User>> {
        return try {
            if (query.isBlank()) return AppResult.Success(emptyList())

            val search = query.trim().lowercase()
            val snapshot = firestore.collection("users").get()

            val users = snapshot.documents
                .map { it.data<User>() }
                .filter { it.id != auth.currentUser?.uid }
                .filter {
                    it.firstName.contains(search, ignoreCase = true) ||
                            it.lastName.contains(search, ignoreCase = true) ||
                            "${it.firstName} ${it.lastName}".contains(search, ignoreCase = true)
                }

            AppResult.Success(users)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            AppResult.Failure(AppError.Unknown())
        }
    }
}