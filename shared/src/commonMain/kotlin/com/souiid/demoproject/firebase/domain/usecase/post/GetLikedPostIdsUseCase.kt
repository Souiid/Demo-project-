package com.souiid.demoproject.firebase.domain.usecase.post

import com.souiid.demoproject.firebase.domain.model.AppResult
import com.souiid.demoproject.firebase.domain.repository.IPostRepository

class GetLikedPostIdsUseCase(
    private val postRepository: IPostRepository
) {
    suspend operator fun invoke(postIds: List<String>): AppResult<Set<String>> {
        return postRepository.getLikedPostIds(postIds)
    }
}