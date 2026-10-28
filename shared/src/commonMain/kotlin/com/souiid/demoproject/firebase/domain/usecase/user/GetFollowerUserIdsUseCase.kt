package com.souiid.demoproject.firebase.domain.usecase.user

import com.souiid.demoproject.firebase.domain.model.AppResult
import com.souiid.demoproject.firebase.domain.repository.IUserRepository

class GetFollowerUserIdsUseCase(
    private val userRepository: IUserRepository
) {
    suspend operator fun invoke(userId: String): AppResult<Set<String>> {
        return userRepository.getFollowerUserIds(userId)
    }
}