package com.souiid.demoproject.domain.usecase.user

import com.souiid.demoproject.domain.model.AppResult
import com.souiid.demoproject.domain.repository.IUserRepository

class GetFollowingUserIdsUseCase(
    private val userRepository: IUserRepository
) {
    suspend operator fun invoke(): AppResult<Set<String>> {
        return userRepository.getFollowingUserIds()
    }
}