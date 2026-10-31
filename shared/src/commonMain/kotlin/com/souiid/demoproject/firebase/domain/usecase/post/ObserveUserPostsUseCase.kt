package com.souiid.demoproject.firebase.domain.usecase.post

import com.souiid.demoproject.firebase.domain.repository.IPostRepository

class ObserveUserPostsUseCase(
    private val postRepository: IPostRepository
) {
    operator fun invoke(userId: String) = postRepository.observeUserPosts(userId)
}