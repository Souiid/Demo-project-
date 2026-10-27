package com.souiid.demoproject.data

import com.souiid.demoproject.domain.model.AppNotification
import com.souiid.demoproject.domain.repository.INotificationRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.FieldValue
import dev.gitlive.firebase.firestore.Timestamp
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.messaging.messaging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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

    override suspend fun deleteToken(userId: String) {
        firestore.collection("users").document(userId).updateFields {
            "fcmToken" to FieldValue.delete
        }
    }

    override fun observeNotifications(userId: String): Flow<List<AppNotification>> {
        return firestore.collection("users").document(userId)
            .collection("notifications")
            .orderBy("createdAt")
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { document ->
                    AppNotification(
                        id = document.id,
                        type = document.get("type"),
                        title = document.get("title"),
                        body = document.get("body"),
                        userId = document.get("userId"),
                        postId = document.get("postId"),
                        createdAt = document.get<Timestamp>("createdAt").seconds,
                        isRead = document.get<Boolean?>("isRead") ?: false
                    )
                }.sortedByDescending { it.createdAt }
            }
    }

    override suspend fun markNotificationsAsRead(userId: String) {
        val notifications = firestore.collection("users").document(userId)
            .collection("notifications").get()

        notifications.documents
            .filter { it.get<Boolean?>("isRead") != true }
            .chunked(400)
            .forEach { documents ->
                val batch = firestore.batch()

                documents.forEach { document ->
                    batch.updateFields(document.reference) {
                        "isRead" to true
                    }
                }

                batch.commit()
            }
    }
}