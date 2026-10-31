package com.souiid.demoproject.firebase.domain.usecase.user

import com.souiid.demoproject.firebase.domain.repository.IUserRepository
import kotlinx.coroutines.flow.combine

class ObserveProfileFollowsUseCase(
    private val userRepository: IUserRepository
) {
    operator fun invoke(userId: String) = combine(
        userRepository.observeFollowerUserIds(userId),
        userRepository.observeFollowingUserIds(userId)
    ) { followers, following ->
        followers.size to following.size
    }
}