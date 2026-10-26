package com.souiid.demoproject.domain.usecase.user

import com.souiid.demoproject.domain.model.AppResult
import com.souiid.demoproject.domain.repository.IUserRepository

class GetFollowerUserIdsUseCase(
    private val userRepository: IUserRepository
) {
    suspend operator fun invoke(userId: String): AppResult<Set<String>> {
        return userRepository.getFollowerUserIds(userId)
    }
}