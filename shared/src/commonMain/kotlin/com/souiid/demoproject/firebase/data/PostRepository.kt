package com.souiid.demoproject.firebase.data

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

    private val functions = Firebase.functions
    private val firestore = Firebase.firestore

    override suspend fun createPost(content: String): AppResult<Unit> {
        return try {
            functions.httpsCallable("createPost").invoke(mapOf("content" to content))
            AppResult.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            AppResult.Failure(AppError.Unknown())
        }
    }

    override suspend fun getFeedPosts(authorIds: List<String>): AppResult<List<Post>> {
        return try {
            if (authorIds.isEmpty()) return AppResult.Success(emptyList())

            val posts = mutableListOf<Post>()

            authorIds.distinct().chunked(30).forEach { ids ->
                val snapshot = firestore.collection("posts")
                    .where { "authorId" inArray ids }
                    .get()

                posts.addAll(snapshot.documents.map { document ->
                    Post(
                        id = document.id,
                        authorId = document.get("authorId"),
                        content = document.get("content"),
                        createdAt = document.get<Timestamp>("createdAt").seconds * 1000,
                        likesCount = document.get("likesCount")
                    )
                })
            }

            AppResult.Success(posts.sortedByDescending { it.createdAt })
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            AppResult.Failure(AppError.Unknown())
        }
    }

    override suspend fun getUserPosts(userId: String): AppResult<List<Post>> {
        return getFeedPosts(listOf(userId))
    }

    override suspend fun likePost(postId: String): AppResult<Unit> {
        return try {
            Firebase.functions.httpsCallable("likePost").invoke(mapOf("postId" to postId))
            AppResult.Success(Unit)
        } catch (e: Exception) {
            AppResult.Failure(AppError.Unknown())
        }
    }

    override suspend fun unlikePost(postId: String): AppResult<Unit> {
        return try {
            Firebase.functions.httpsCallable("unlikePost").invoke(mapOf("postId" to postId))
            AppResult.Success(Unit)
        } catch (e: Exception) {
            AppResult.Failure(AppError.Unknown())
        }
    }

    override suspend fun getLikedPostIds(postIds: List<String>): AppResult<Set<String>> {
        return try {
            val userId = Firebase.auth.currentUser?.uid
                ?: return AppResult.Failure(AppError.Unknown())

            val likedPostIds = mutableSetOf<String>()

            for (postId in postIds.distinct()) {
                val like = Firebase.firestore
                    .collection("posts")
                    .document(postId)
                    .collection("likes")
                    .document(userId)
                    .get()

                if (like.exists) likedPostIds.add(postId)
            }

            AppResult.Success(likedPostIds)
        } catch (e: Exception) {
            AppResult.Failure(AppError.Unknown())
        }
    }

    override fun observeFeedPosts(authorIds: List<String>): Flow<List<Post>> {
        if (authorIds.isEmpty()) return flowOf(emptyList())

        val flows = authorIds.distinct().chunked(30).map { ids ->
            firestore.collection("posts")
                .where { "authorId" inArray ids }
                .snapshots
                .map { snapshot ->
                    snapshot.documents.map { document ->
                        Post(
                            id = document.id,
                            authorId = document.get("authorId"),
                            content = document.get("content"),
                            createdAt = document.get<Timestamp>("createdAt").seconds * 1000,
                            likesCount = document.get("likesCount")
                        )
                    }
                }
        }

        return combine(flows) { results ->
            results.flatMap { it }.sortedByDescending { it.createdAt }
        }
    }
}