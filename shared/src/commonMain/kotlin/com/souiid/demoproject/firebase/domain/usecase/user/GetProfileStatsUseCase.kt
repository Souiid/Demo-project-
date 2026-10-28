package com.souiid.demoproject.firebase.domain.usecase.user

import com.souiid.demoproject.firebase.domain.model.AppResult
import com.souiid.demoproject.firebase.domain.model.ProfileStats
import com.souiid.demoproject.firebase.domain.usecase.post.GetUserPostsUseCase

class GetProfileStatsUseCase(
    private val getFollowerUserIdsUseCase: GetFollowerUserIdsUseCase,
    private val getFollowingUserIdsUseCase: GetFollowingUserIdsUseCase,
    private val getUserPostsUseCase: GetUserPostsUseCase
) {
    suspend operator fun invoke(userId: String): AppResult<ProfileStats> {
        val followers = when (val result = getFollowerUserIdsUseCase(userId)) {
            is AppResult.Success -> result.data.size
            is AppResult.Failure -> return AppResult.Failure(result.error)
        }

        val following = when (val result = getFollowingUserIdsUseCase(userId)) {
            is AppResult.Success -> result.data.size
            is AppResult.Failure -> return AppResult.Failure(result.error)
        }

        val posts = when (val result = getUserPostsUseCase(userId)) {
            is AppResult.Success -> result.data.size
            is AppResult.Failure -> return AppResult.Failure(result.error)
        }

        return AppResult.Success(
            ProfileStats(
                posts = posts,
                followers = followers,
                following = following
            )
        )
    }
}