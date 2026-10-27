package com.souiid.demoproject.platform

import platform.UserNotifications.UNUserNotificationCenter

actual fun updateAppBadge(count: Int) {
    UNUserNotificationCenter.currentNotificationCenter()
        .setBadgeCount(count.toLong()) { error ->
            if (error != null) {
                println("Failed to update app badge: $error")
            }
        }
}