package com.souiid.demoproject.domain.usecase.post

import com.souiid.demoproject.domain.model.AppError
import com.souiid.demoproject.domain.model.AppResult
import com.souiid.demoproject.domain.model.Post
import com.souiid.demoproject.domain.repository.IPostRepository
import com.souiid.demoproject.domain.usecase.user.GetFollowingUserIdsUseCase
import com.souiid.demoproject.domain.usecase.user.ObserveCurrentUserUseCase

class GetFeedPostsUseCase(
    private val postRepository: IPostRepository,
    private val getFollowingUserIdsUseCase: GetFollowingUserIdsUseCase,
    private val observeCurrentUserUseCase: ObserveCurrentUserUseCase
) {
    suspend operator fun invoke(): AppResult<List<Post>> {
        val userId = observeCurrentUserUseCase().value?.id
            ?: return AppResult.Failure(AppError.Unknown())

        return when (val result = getFollowingUserIdsUseCase(userId)) {
            is AppResult.Success -> {
                val authorIds = (result.data + userId).toList()
                postRepository.getFeedPosts(authorIds)
            }
            is AppResult.Failure -> AppResult.Failure(result.error)
        }
    }
}