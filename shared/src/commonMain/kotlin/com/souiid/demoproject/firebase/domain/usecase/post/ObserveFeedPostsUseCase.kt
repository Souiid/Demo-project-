package com.souiid.demoproject.firebase.domain.usecase.post

import com.souiid.demoproject.firebase.domain.model.AppResult
import com.souiid.demoproject.firebase.domain.model.Post
import com.souiid.demoproject.firebase.domain.repository.IPostRepository
import com.souiid.demoproject.firebase.domain.usecase.user.GetFollowingUserIdsUseCase
import com.souiid.demoproject.firebase.domain.usecase.user.ObserveCurrentUserUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlin.collections.plus

class ObserveFeedPostsUseCase(
    private val postRepository: IPostRepository,
    private val getFollowingUserIdsUseCase: GetFollowingUserIdsUseCase,
    private val observeCurrentUserUseCase: ObserveCurrentUserUseCase
) {
    operator fun invoke(): Flow<List<Post>> = flow {
        val userId = observeCurrentUserUseCase().value?.id
            ?: throw IllegalStateException("User not authenticated")

        when (val result = getFollowingUserIdsUseCase(userId)) {
            is AppResult.Success -> {
                val authorIds = (result.data + userId).toList()
                emitAll(postRepository.observeFeedPosts(authorIds))
            }
            is AppResult.Failure -> throw IllegalStateException("Failed to load following users")
        }
    }
}