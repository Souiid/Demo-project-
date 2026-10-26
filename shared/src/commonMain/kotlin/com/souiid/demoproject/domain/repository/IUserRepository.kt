package com.souiid.demoproject.domain.repository

import com.souiid.demoproject.domain.model.AppResult
import com.souiid.demoproject.domain.model.ProfileStats
import com.souiid.demoproject.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface IUserRepository {
    val currentUser: StateFlow<User?>
    suspend fun fetchUser(userId: String): AppResult<User>
    suspend fun updateUser(firstName: String, lastName: String, description: String, photoUrl: String? = null): AppResult<Unit>
    suspend fun uploadPhoto(uri: String): AppResult<String>
    suspend fun searchUsers(query: String): AppResult<List<User>>
    suspend fun followUser(targetUserId: String): AppResult<Unit>
    suspend fun unfollowUser(targetUserId: String): AppResult<Unit>
    suspend fun getFollowingUserIds(userId: String): AppResult<Set<String>>
    suspend fun getFollowingUsers(userId: String): AppResult<List<User>>
    suspend fun getFollowerUserIds(userId: String): AppResult<Set<String>>

}