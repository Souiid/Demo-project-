package com.souiid.demoproject.data

import com.souiid.demoproject.domain.model.AppError
import com.souiid.demoproject.domain.model.AppResult
import com.souiid.demoproject.domain.model.Post
import com.souiid.demoproject.domain.repository.IPostRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.Timestamp
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.functions.functions
import kotlinx.coroutines.CancellationException

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
}