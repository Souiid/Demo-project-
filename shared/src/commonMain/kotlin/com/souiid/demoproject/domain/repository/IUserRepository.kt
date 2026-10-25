package com.souiid.demoproject.domain.repository

import com.souiid.demoproject.domain.model.AppResult
import com.souiid.demoproject.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface IUserRepository {
    val currentUser: StateFlow<User?>
    suspend fun fetchUser(userId: String): AppResult<User>
    suspend fun updateUser(firstName: String, lastName: String, description: String, photoUrl: String? = null): AppResult<Unit>
    suspend fun uploadPhoto(uri: String): AppResult<String>

}