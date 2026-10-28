package com.souiid.demoproject.firebase.domain.usecase.user

import com.souiid.demoproject.firebase.domain.model.AppResult
import com.souiid.demoproject.firebase.domain.repository.IUserRepository

class UnfollowUserUseCase(
    private val userRepository: IUserRepository
) {
    suspend operator fun invoke(targetUserId: String): AppResult<Unit> {
        return userRepository.unfollowUser(targetUserId)
    }
}