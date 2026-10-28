package com.souiid.demoproject.firebase.domain.usecase.post

import com.souiid.demoproject.firebase.domain.model.AppResult
import com.souiid.demoproject.firebase.domain.model.Post
import com.souiid.demoproject.firebase.domain.repository.IPostRepository

class GetUserPostsUseCase(
    private val postRepository: IPostRepository
) {
    suspend operator fun invoke(userId: String): AppResult<List<Post>> {
        return postRepository.getUserPosts(userId)
    }
}