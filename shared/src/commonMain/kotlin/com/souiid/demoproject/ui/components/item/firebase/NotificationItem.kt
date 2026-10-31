package com.souiid.demoproject.ui.components.item.firebase

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.souiid.demoproject.firebase.domain.model.AppNotification
import com.souiid.demoproject.ui.components.others.AppIcon
import com.souiid.demoproject.ui.components.text.TText
import com.souiid.demoproject.ui.components.text.TTextStyle
import com.souiid.demoproject.ui.theme.Dimens
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

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

    val formattedDate = "${date.day.toString().padStart(2, '0')}/" +
            "${date.month.number.toString().padStart(2, '0')}/${date.year} " +
            "${date.hour.toString().padStart(2, '0')}:" +
            date.minute.toString().padStart(2, '0')

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = Dimens.mediumSpace),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(Dimens.mediumSpace)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(Dimens.progressIndicatorSize)
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Dimens.tinySpace)
        ) {
            TText(
                text = notification.title,
                style = TTextStyle.ITEM_TITLE
            )

            TText(
                text = notification.body,
                style = TTextStyle.BODY
            )

            TText(
                text = formattedDate,
                style = TTextStyle.DESCRIPTION,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}