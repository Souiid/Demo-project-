package com.souiid.demoproject.firebase.domain.usecase.post

import com.souiid.demoproject.firebase.domain.model.Post
import com.souiid.demoproject.firebase.domain.repository.IPostRepository
import com.souiid.demoproject.firebase.domain.usecase.user.ObserveCurrentUserUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest

class ObserveFeedPostsUseCase(
    private val postRepository: IPostRepository,
    private val observeCurrentUserUseCase: ObserveCurrentUserUseCase
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(followingUserIds: Flow<Set<String>>): Flow<List<Post>> {
        return followingUserIds
            .distinctUntilChanged()
            .flatMapLatest { followingIds ->
                val userId = observeCurrentUserUseCase().value?.id
                    ?: throw IllegalStateException("User not authenticated")

                postRepository.observeFeedPosts((followingIds + userId).toList())
            }
    }
}