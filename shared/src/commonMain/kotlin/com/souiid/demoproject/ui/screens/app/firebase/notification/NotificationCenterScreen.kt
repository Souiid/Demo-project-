package com.souiid.demoproject.ui.screens.app.firebase.notification

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.souiid.demoproject.ui.components.item.firebase.NotificationItem
import com.souiid.demoproject.ui.components.others.TScreen
import com.souiid.demoproject.ui.components.text.TText
import com.souiid.demoproject.ui.components.text.TTextStyle
import com.souiid.demoproject.ui.theme.Dimens
import org.koin.compose.koinInject

@Composable
fun NotificationCenterScreen() {
    val viewModel: NotificationCenterViewModel = koinInject()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    TScreen(horizontalAlignment = Alignment.Start) {
        TText(
            text = "Notifications",
            style = TTextStyle.TITLE,
            modifier = Modifier.padding(vertical = Dimens.standardPadding)
        )

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                notifications.isEmpty() -> {
                    TText(
                        text = "No notifications yet",
                        style = TTextStyle.BODY,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(notifications, key = { it.id }) { notification ->
                            NotificationItem(notification)

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = Dimens.standardPadding)
                            )
                        }
                    }
                }
            }
        }
    }
}