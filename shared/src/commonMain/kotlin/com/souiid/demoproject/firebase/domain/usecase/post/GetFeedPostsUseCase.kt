package com.souiid.demoproject.firebase.domain.usecase.post

import com.souiid.demoproject.firebase.domain.model.AppError
import com.souiid.demoproject.firebase.domain.model.AppResult
import com.souiid.demoproject.firebase.domain.model.Post
import com.souiid.demoproject.firebase.domain.repository.IPostRepository
import com.souiid.demoproject.firebase.domain.usecase.user.GetFollowingUserIdsUseCase
import com.souiid.demoproject.firebase.domain.usecase.user.ObserveCurrentUserUseCase
import kotlin.collections.plus

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