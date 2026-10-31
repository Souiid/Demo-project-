package com.souiid.demoproject.firebase.data.repository

import com.souiid.demoproject.firebase.data.utils.FirebaseConstants
import com.souiid.demoproject.firebase.data.utils.likesCollection
import com.souiid.demoproject.firebase.data.utils.postsCollection
import com.souiid.demoproject.firebase.domain.model.AppError
import com.souiid.demoproject.firebase.domain.model.AppResult
import com.souiid.demoproject.firebase.domain.model.Post
import com.souiid.demoproject.firebase.domain.repository.IPostRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.Timestamp
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.functions.functions
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class PostRepository : IPostRepository {

    private val functions = Firebase.functions(FirebaseConstants.REGION)
    private val firestore = Firebase.firestore

    override suspend fun createPost(content: String): AppResult<Unit> {
        return try {
            functions.httpsCallable(FirebaseConstants.Functions.CREATE_POST)
                .invoke(mapOf(FirebaseConstants.Fields.CONTENT to content))
            AppResult.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            AppResult.Failure(AppError.Unknown())
        }
    }

    override suspend fun deletePost(postId: String): AppResult<Unit> {
        return try {
            functions.httpsCallable(FirebaseConstants.Functions.DELETE_POST)
                .invoke(mapOf(FirebaseConstants.Fields.POST_ID to postId))
            AppResult.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            AppResult.Failure(AppError.Unknown())
        }
    }

    override suspend fun getFeedPosts(authorIds: List<String>): AppResult<List<Post>> {
        return try {
            if (authorIds.isEmpty()) return AppResult.Success(emptyList())

            val posts = mutableListOf<Post>()

            authorIds.distinct().chunked(30).forEach { ids ->
                val snapshot = firestore.postsCollection()
                    .where { FirebaseConstants.Fields.AUTHOR_ID inArray ids }
                    .get()

                posts.addAll(snapshot.documents.map { document ->
                    Post(
                        id = document.id,
                        authorId = document.get(FirebaseConstants.Fields.AUTHOR_ID),
                        content = document.get(FirebaseConstants.Fields.CONTENT),
                        createdAt = document.get<Timestamp>(FirebaseConstants.Fields.CREATED_AT).seconds * 1000,
                        likesCount = document.get(FirebaseConstants.Fields.LIKES_COUNT)
                    )
                })
            }

            AppResult.Success(posts.sortedByDescending { it.createdAt })
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            AppResult.Failure(AppError.Unknown())
        }
    }

    override suspend fun getUserPosts(userId: String): AppResult<List<Post>> {
        return getFeedPosts(listOf(userId))
    }

    override suspend fun likePost(postId: String): AppResult<Unit> {
        return try {
            functions.httpsCallable(FirebaseConstants.Functions.LIKE_POST)
                .invoke(mapOf(FirebaseConstants.Fields.POST_ID to postId))
            AppResult.Success(Unit)
        } catch (_: Exception) {
            AppResult.Failure(AppError.Unknown())
        }
    }

    override suspend fun unlikePost(postId: String): AppResult<Unit> {
        return try {
            functions.httpsCallable(FirebaseConstants.Functions.UNLIKE_POST)
                .invoke(mapOf(FirebaseConstants.Fields.POST_ID to postId))
            AppResult.Success(Unit)
        } catch (_: Exception) {
            AppResult.Failure(AppError.Unknown())
        }
    }

    override suspend fun getLikedPostIds(postIds: List<String>): AppResult<Set<String>> {
        return try {
            val userId = Firebase.auth.currentUser?.uid
                ?: return AppResult.Failure(AppError.Unknown())

            val likedPostIds = mutableSetOf<String>()

            for (postId in postIds.distinct()) {
                val like = firestore.likesCollection(postId)
                    .document(userId)
                    .get()

                if (like.exists) likedPostIds.add(postId)
            }

            AppResult.Success(likedPostIds)
        } catch (_: Exception) {
            AppResult.Failure(AppError.Unknown())
        }
    }

    override fun observeFeedPosts(authorIds: List<String>): Flow<List<Post>> {
        if (authorIds.isEmpty()) return flowOf(emptyList())

        val flows = authorIds.distinct().chunked(30).map { ids ->
            firestore.postsCollection()
                .where { FirebaseConstants.Fields.AUTHOR_ID inArray ids }
                .snapshots
                .map { snapshot ->
                    snapshot.documents.map { document ->
                        Post(
                            id = document.id,
                            authorId = document.get(FirebaseConstants.Fields.AUTHOR_ID),
                            content = document.get(FirebaseConstants.Fields.CONTENT),
                            createdAt = document.get<Timestamp>(FirebaseConstants.Fields.CREATED_AT).seconds * 1000,
                            likesCount = document.get(FirebaseConstants.Fields.LIKES_COUNT)
                        )
                    }
                }
        }

        return combine(flows) { results ->
            results.flatMap { it }.sortedByDescending { it.createdAt }
        }
    }

    override fun observeUserPosts(userId: String): Flow<List<Post>> {
        return observeFeedPosts(listOf(userId))
    }
}