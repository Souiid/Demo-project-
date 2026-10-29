package com.souiid.demoproject.ui.components.item.firebase

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.souiid.demoproject.firebase.domain.model.AppNotification
import com.souiid.demoproject.ui.components.others.AppIcon
import com.souiid.demoproject.ui.components.text.TText
import com.souiid.demoproject.ui.components.text.TTextStyle
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun NotificationItem(notification: AppNotification) {
    val icon = when (notification.type) {
        "NEW_FOLLOWER" -> AppIcon.NotificationFollower
        "NEW_LIKE" -> AppIcon.NotificationLike
        "NEW_POST" -> AppIcon.NotificationPost
        else -> AppIcon.Notifications
    }

    val date = Instant.fromEpochSeconds(notification.createdAt)
        .toLocalDateTime(TimeZone.currentSystemDefault())

    val formattedDate = "${date.dayOfMonth.toString().padStart(2, '0')}/" +
            "${date.monthNumber.toString().padStart(2, '0')}/${date.year} " +
            "${date.hour.toString().padStart(2, '0')}:" +
            date.minute.toString().padStart(2, '0')

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            TText(text = notification.title, style = TTextStyle.SUBTITLE)
            TText(text = notification.body, style = TTextStyle.BODY)
            TText(text = formattedDate, style = TTextStyle.DESCRIPTION)
        }
    }
}