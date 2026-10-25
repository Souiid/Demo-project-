package com.souiid.demoproject.domain.usecase.user

import com.souiid.demoproject.domain.model.AppResult
import com.souiid.demoproject.domain.repository.IUserRepository

class FollowUserUseCase(
    private val userRepository: IUserRepository
) {
    suspend operator fun invoke(targetUserId: String): AppResult<Unit> {
        return userRepository.followUser(targetUserId)
    }
}