package com.souiid.demoproject.domain.usecase.user

import com.souiid.demoproject.domain.model.AppResult
import com.souiid.demoproject.domain.model.User
import com.souiid.demoproject.domain.repository.IUserRepository

class GetFollowingUsersUseCase(
    private val userRepository: IUserRepository
) {
    suspend operator fun invoke(userId: String): AppResult<List<User>> {
        return userRepository.getFollowingUsers(userId)
    }
}