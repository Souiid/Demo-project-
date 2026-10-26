package com.souiid.demoproject.domain.usecase.post

import com.souiid.demoproject.domain.model.AppResult
import com.souiid.demoproject.domain.model.Post
import com.souiid.demoproject.domain.repository.IPostRepository

class GetUserPostsUseCase(
    private val postRepository: IPostRepository
) {
    suspend operator fun invoke(userId: String): AppResult<List<Post>> {
        return postRepository.getUserPosts(userId)
    }
}