package com.souiid.demoproject.platform

import android.app.NotificationManager
import android.content.Context

actual fun updateAppBadge(count: Int) {
    if (count != 0) return

    val context = AppContextProvider.context
    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    manager.cancelAll()
}