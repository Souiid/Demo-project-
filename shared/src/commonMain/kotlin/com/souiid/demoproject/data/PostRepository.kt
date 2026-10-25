package com.souiid.demoproject.data

import com.souiid.demoproject.domain.model.AppError
import com.souiid.demoproject.domain.model.AppResult
import com.souiid.demoproject.domain.repository.IPostRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.functions.functions
import kotlinx.coroutines.CancellationException

class PostRepository : IPostRepository {

    private val functions = Firebase.functions

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
}