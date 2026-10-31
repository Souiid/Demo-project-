package com.souiid.demoproject.firebase.data.repository

import com.souiid.demoproject.firebase.data.utils.FirebaseConstants
import com.souiid.demoproject.firebase.data.utils.notificationsCollection
import com.souiid.demoproject.firebase.data.utils.userDocument
import com.souiid.demoproject.firebase.domain.model.AppNotification
import com.souiid.demoproject.firebase.domain.repository.INotificationRepository
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
    private val tokenField = FirebaseConstants.Fields.FCM_TOKEN

    override suspend fun getToken(): String = messaging.getToken()

    override suspend fun saveToken(userId: String) {
        val token = messaging.getToken()
        if (token.isBlank()) return

        firestore.userDocument(userId).updateFields {
            tokenField to token
        }
    }

    override suspend fun deleteToken(userId: String) {
        firestore.userDocument(userId).updateFields {
            tokenField to FieldValue.delete
        }
    }

    override fun observeNotifications(userId: String): Flow<List<AppNotification>> {
        return firestore.notificationsCollection(userId)
            .orderBy(FirebaseConstants.Fields.CREATED_AT)
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { document ->
                    AppNotification(
                        id = document.id,
                        type = document.get(FirebaseConstants.Fields.TYPE),
                        title = document.get(FirebaseConstants.Fields.TITLE),
                        body = document.get(FirebaseConstants.Fields.BODY),
                        userId = document.get(FirebaseConstants.Fields.USER_ID),
                        postId = document.get(FirebaseConstants.Fields.POST_ID),
                        createdAt = document.get<Timestamp>(FirebaseConstants.Fields.CREATED_AT).seconds,
                        isRead = document.get<Boolean?>(FirebaseConstants.Fields.IS_READ) ?: false
                    )
                }.sortedByDescending { it.createdAt }
            }
    }

    override suspend fun markNotificationsAsRead(userId: String) {
        val notifications = firestore.notificationsCollection(userId).get()

        notifications.documents
            .filter { it.get<Boolean?>(FirebaseConstants.Fields.IS_READ) != true }
            .chunked(400)
            .forEach { documents ->
                val batch = firestore.batch()

                documents.forEach { document ->
                    batch.updateFields(document.reference) {
                        FirebaseConstants.Fields.IS_READ to true
                    }
                }

                batch.commit()
            }
    }
}