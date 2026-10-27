package com.souiid.demoproject.domain.repository

interface INotificationRepository {
    suspend fun getToken(): String
    suspend fun saveToken(userId: String)
}