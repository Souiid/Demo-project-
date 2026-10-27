package com.souiid.demoproject.domain.repository

import com.souiid.demoproject.domain.model.AppNotification
import kotlinx.coroutines.flow.Flow

interface INotificationRepository {
    suspend fun getToken(): String
    suspend fun saveToken(userId: String)
    suspend fun deleteToken(userId: String)
    fun observeNotifications(userId: String): Flow<List<AppNotification>>

}