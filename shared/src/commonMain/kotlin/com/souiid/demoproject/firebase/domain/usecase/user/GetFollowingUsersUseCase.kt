package com.souiid.demoproject.firebase.domain.usecase.user

import com.souiid.demoproject.firebase.domain.model.AppResult
import com.souiid.demoproject.firebase.domain.model.User
import com.souiid.demoproject.firebase.domain.repository.IUserRepository

class GetFollowingUsersUseCase(
    private val userRepository: IUserRepository
) {
    suspend operator fun invoke(userId: String): AppResult<List<User>> {
        return userRepository.getFollowingUsers(userId)
    }
}