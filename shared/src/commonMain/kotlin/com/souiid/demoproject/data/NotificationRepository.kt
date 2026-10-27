package com.souiid.demoproject.data

import com.souiid.demoproject.domain.repository.INotificationRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.messaging.messaging

class NotificationRepository : INotificationRepository {

    private val messaging = Firebase.messaging
    private val firestore = Firebase.firestore

    override suspend fun getToken(): String = messaging.getToken()

    override suspend fun saveToken(userId: String) {
        val token = messaging.getToken()
        if (token.isBlank()) return

        firestore.collection("users").document(userId).updateFields {
            "fcmToken" to token
        }
    }
}