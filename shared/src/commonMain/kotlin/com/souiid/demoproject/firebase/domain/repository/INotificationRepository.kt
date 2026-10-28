package com.souiid.demoproject.firebase.domain.repository

import com.souiid.demoproject.firebase.domain.model.AppNotification
import kotlinx.coroutines.flow.Flow

interface INotificationRepository {
    suspend fun getToken(): String
    suspend fun saveToken(userId: String)
    suspend fun deleteToken(userId: String)
    fun observeNotifications(userId: String): Flow<List<AppNotification>>
    suspend fun markNotificationsAsRead(userId: String)
}