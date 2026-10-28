package com.souiid.demoproject.firebase.domain.usecase.user

import com.souiid.demoproject.firebase.domain.model.AppResult
import com.souiid.demoproject.firebase.domain.model.User
import com.souiid.demoproject.firebase.domain.repository.IUserRepository

class SearchUsersUseCase(
    private val userRepository: IUserRepository
) {
    suspend operator fun invoke(query: String): AppResult<List<User>> {
        if (query.isBlank()) return AppResult.Success(emptyList())
        return userRepository.searchUsers(query.trim())
    }
}