package com.souiid.demoproject.domain.usecase.post

import com.souiid.demoproject.domain.model.AppError
import com.souiid.demoproject.domain.model.AppResult
import com.souiid.demoproject.domain.repository.IPostRepository

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