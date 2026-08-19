package com.den.steward.backend.notification

import android.content.Context
import androidx.core.app.NotificationCompat

interface NotificationBase {
    fun showNotification(notificationEntity: NotificationEntity)
    fun buildForegroundNotification(context: Context): android.app.Notification
    val channelId: String
    var builder: NotificationCompat.Builder
}
