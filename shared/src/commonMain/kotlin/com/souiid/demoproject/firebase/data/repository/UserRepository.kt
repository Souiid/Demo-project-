package com.souiid.demoproject.firebase.data.repository

import com.souiid.demoproject.firebase.data.utils.FirebaseConstants
import com.souiid.demoproject.firebase.data.utils.followersCollection
import com.souiid.demoproject.firebase.data.utils.followingCollection
import com.souiid.demoproject.firebase.data.utils.userDocument
import com.souiid.demoproject.firebase.data.utils.usersCollection
import com.souiid.demoproject.firebase.domain.model.AppError
import com.souiid.demoproject.firebase.domain.model.AppResult
import com.souiid.demoproject.firebase.domain.model.User
import com.souiid.demoproject.firebase.domain.repository.IUserRepository
import com.souiid.demoproject.platform.createFileFromUri
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.functions.functions
import dev.gitlive.firebase.storage.storage
import dev.gitlive.firebase.storage.storageMetadata
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlin.coroutines.cancellation.CancellationException

class UserRepository : IUserRepository {

    private val firestore = Firebase.firestore
    private val auth: FirebaseAuth = Firebase.auth
    private val functions = Firebase.functions(FirebaseConstants.REGION)

    private val _currentUser = MutableStateFlow<User?>(null)
    override val currentUser = _currentUser.asStateFlow()

    override suspend fun fetchUser(userId: String): AppResult<User> {
        return try {
            val snapshot = firestore.userDocument(userId).get()

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
        } catch (_: Exception) {
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
                FirebaseConstants.Fields.FIRST_NAME to firstName,
                FirebaseConstants.Fields.LAST_NAME to lastName,
                FirebaseConstants.Fields.DESCRIPTION to description
            )
            if (photoUrl != null) updates[FirebaseConstants.Fields.PHOTO_URL] = photoUrl

            firestore.userDocument(user.id).update(updates)

            _currentUser.value = user.copy(
                firstName = firstName,
                lastName = lastName,
                description = description,
                photoUrl = photoUrl ?: user.photoUrl
            )

            AppResult.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            AppResult.Failure(AppError.Unknown())
        }
    }

    override suspend fun uploadPhoto(uri: String, mimeType: String): AppResult<String> {
        val userId = Firebase.auth.currentUser?.uid ?: return AppResult.Failure(AppError.Unknown())
        return try {
            val reference = Firebase.storage.reference.child(
                FirebaseConstants.Storage.profilePhotoPath(userId)
            )
            reference.putFile(createFileFromUri(uri), storageMetadata {
                contentType = mimeType
            })
            AppResult.Success(reference.getDownloadUrl())
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            AppResult.Failure(AppError.Unknown())
        }
    }

    override suspend fun searchUsers(query: String): AppResult<List<User>> {
        return try {
            if (query.isBlank()) return AppResult.Success(emptyList())

            val search = query.trim().lowercase()
            val snapshot = firestore.usersCollection().get()

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
        } catch (_: Exception) {
            AppResult.Failure(AppError.Unknown())
        }
    }

    override suspend fun followUser(targetUserId: String): AppResult<Unit> {
        return try {
            functions.httpsCallable(FirebaseConstants.Functions.FOLLOW_USER)
                .invoke(mapOf(FirebaseConstants.Fields.TARGET_USER_ID to targetUserId))
            AppResult.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            AppResult.Failure(AppError.Unknown())
        }
    }

    override suspend fun unfollowUser(targetUserId: String): AppResult<Unit> {
        return try {
            functions.httpsCallable(FirebaseConstants.Functions.UNFOLLOW_USER)
                .invoke(mapOf(FirebaseConstants.Fields.TARGET_USER_ID to targetUserId))
            AppResult.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            AppResult.Failure(AppError.Unknown())
        }
    }

    override suspend fun getFollowingUserIds(userId: String): AppResult<Set<String>> {
        return try {
            val snapshot = firestore.followingCollection(userId).get()
            AppResult.Success(snapshot.documents.map { it.id }.toSet())
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            AppResult.Failure(AppError.Unknown())
        }
    }

    override suspend fun getFollowingUsers(userId: String): AppResult<List<User>> {
        return try {
            val following = firestore.followingCollection(userId).get()

            val users = following.documents.mapNotNull { document ->
                val snapshot = firestore.userDocument(document.id).get()
                if (snapshot.exists) snapshot.data<User>() else null
            }

            AppResult.Success(users)
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            AppResult.Failure(AppError.Unknown())
        }
    }

    override suspend fun getFollowerUserIds(userId: String): AppResult<Set<String>> {
        return try {
            val snapshot = firestore.followersCollection(userId).get()
            AppResult.Success(snapshot.documents.map { it.id }.toSet())
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            AppResult.Failure(AppError.Unknown())
        }
    }

    override fun observeFollowerUserIds(userId: String): Flow<Set<String>> =
        firestore.followersCollection(userId)
            .snapshots.map { snapshot -> snapshot.documents.map { it.id }.toSet() }

    override fun observeFollowingUserIds(userId: String): Flow<Set<String>> =
        firestore.followingCollection(userId)
            .snapshots.map { snapshot -> snapshot.documents.map { it.id }.toSet() }
}