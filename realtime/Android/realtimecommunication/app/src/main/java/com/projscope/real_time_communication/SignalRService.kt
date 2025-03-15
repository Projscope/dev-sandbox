package com.projscope.real_time_communication

import android.R
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.util.Log
import androidx.core.app.NotificationCompat
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder


class SignalRService {
    private var hubConnection: HubConnection? = null
    private lateinit var context: Context;

    @SuppressLint("CheckResult")
    fun startConnection(baseContext: Context) {
        hubConnection = HubConnectionBuilder.create("wss://api.dev-sandbox.dev/com-hub")
            .build()

        context = baseContext;

        val notificationHelper = NotificationHelper(context)
        notificationHelper.createNotificationChannel()


        // Start the connection
        hubConnection?.start()?.subscribe({
            Log.d("SignalR", "Connected successfully!")

            // Join Group
            hubConnection?.invoke("JoinGroup", "api_;4+ospGZs]", "channel_AIuO1NqAeNuf")
            Log.d("SignalR", "Joining group")

        }, { error ->
            Log.e("SignalR", "Error connecting: ${error.message}")
        })


        // Listen for messages from the server
        hubConnection?.on("ReceiveMessage", { message: String ->
            Log.d("SignalR", "Received: $message")

            val notificationTitle = "Realtime message"
            val notificationContent = message
            notificationHelper.showHeadsUpNotification(notificationTitle, notificationContent)
        }, String::class.java)
    }

    fun stopConnection() {
        hubConnection?.stop()
    }

    fun sendMessage(user: String, message: String) {
        hubConnection?.invoke("SendMessage", user, message)
    }
}
