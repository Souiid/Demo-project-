package com.souiid.demoproject.firebase.domain.usecase.post

import com.souiid.demoproject.firebase.domain.model.AppError
import com.souiid.demoproject.firebase.domain.model.AppResult
import com.souiid.demoproject.firebase.domain.repository.IPostRepository

class CreatePostUseCase(
    private val postRepository: IPostRepository
) {
    suspend operator fun invoke(content: String): AppResult<Unit> {
        val trimmedContent = content.trim()

        if (trimmedContent.isBlank() || trimmedContent.length > 1000) {
            return AppResult.Failure(AppError.Unknown())
        }

        return postRepository.createPost(trimmedContent)
    }
}