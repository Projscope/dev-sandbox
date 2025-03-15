package com.projscope.real_time_communication

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat

class NotificationHelper(private val context: Context) {

    // Create notification channel for Android 8.0+
    fun createNotificationChannel() {
        val channelId = "channel_id"
        val channelName = "Heads-up Notification Channel"
        val channelDescription = "Channel for heads-up notifications"
        val importance = NotificationManager.IMPORTANCE_HIGH  // High importance for heads-up

        val channel = NotificationChannel(channelId, channelName, importance).apply {
            description = channelDescription
        }

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    // Show heads-up notification with injected title & content
    fun showHeadsUpNotification(title: String, contentText: String) {
        val builder = NotificationCompat.Builder(context, "channel_id")
            .setSmallIcon(R.drawable.ic_launcher_background)  // Replace with your app icon
            .setContentTitle(title)  // Injected title
            .setContentText(contentText)  // Injected content text
            .setPriority(NotificationCompat.PRIORITY_HIGH)  // High priority for heads-up display
            .setDefaults(Notification.DEFAULT_ALL)
            .setLights(Color.RED, 1000, 1000)  // Optional: Flashing lights
            .setAutoCancel(true)

        // Show notification
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.notify(1, builder.build())
    }
}
