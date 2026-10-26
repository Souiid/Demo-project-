package com.souiid.demoproject.domain.usecase.post

import com.souiid.demoproject.domain.model.AppResult
import com.souiid.demoproject.domain.repository.IPostRepository

class UnlikePostUseCase(
    private val postRepository: IPostRepository
) {
    suspend operator fun invoke(postId: String): AppResult<Unit> {
        return postRepository.unlikePost(postId)
    }
}