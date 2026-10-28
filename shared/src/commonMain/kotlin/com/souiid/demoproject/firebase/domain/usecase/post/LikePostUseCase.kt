package com.souiid.demoproject.firebase.domain.usecase.post

import com.souiid.demoproject.firebase.domain.model.AppResult
import com.souiid.demoproject.firebase.domain.repository.IPostRepository

class LikePostUseCase(
    private val postRepository: IPostRepository
) {
    suspend operator fun invoke(postId: String): AppResult<Unit> {
        return postRepository.likePost(postId)
    }
}