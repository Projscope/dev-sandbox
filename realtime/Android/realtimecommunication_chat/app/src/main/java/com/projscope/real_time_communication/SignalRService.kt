package com.projscope.real_time_communication

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class SignalRService {
    private lateinit var context: Context
    private val hubConnection: HubConnection = HubConnectionBuilder
        .create("wss://api.dev-sandbox.dev/com-hub")
        .build()

    val connectionStatus: BehaviorSubject<Boolean> = BehaviorSubject.createDefault(false)

    @SuppressLint("CheckResult")
    fun startConnection(baseContext: Context) {
        context = baseContext

        val notificationHelper = NotificationHelper(context)
        notificationHelper.createNotificationChannel()

        hubConnection.start()
            .retryWhen { errors ->
                errors.take(3).delay(3, TimeUnit.SECONDS) // Retry up to 3 times
            }
            .subscribe({
                Log.d("SignalR", "Connected successfully!")
                hubConnection.invoke("JoinGroup", "api_;4+ospGZs]", "channel_LQZjp98azInh")
                Log.d("SignalR", "Joining group")
                connectionStatus.onNext(true)
            }, { error ->
                Log.e("SignalR", "Error connecting: ${error.message}")
                connectionStatus.onNext(false)
            })

        hubConnection.on("ReceiveMessage", { message: String ->
            Log.d("SignalR", "Received: $message | Thread: ${Thread.currentThread().name}")

            notificationHelper.showHeadsUpNotification(
                "Realtime message",
                message
            )
        }, String::class.java)
    }

    fun stopConnection() {
        hubConnection.stop()
            .subscribe({
                Log.d("SignalR", "Disconnected successfully")

                // Wait for subscribers before sending onNext(false)
                var attempts = 0
                while (!connectionStatus.hasObservers() && attempts < 10) {
                    Thread.sleep(100) // Wait a bit
                    attempts++
                }

                if (connectionStatus.hasObservers()) {
                    connectionStatus.onNext(false)
                } else {
                    Log.w("SignalR", "No subscribers to receive connection status update after waiting")
                }
            }, { error ->
                Log.e("SignalR", "Error stopping: ${error.message}")
            })
    }

    fun sendMessage(user: String, message: String) {
        if (hubConnection.connectionState == HubConnectionState.CONNECTED) {
            hubConnection.invoke("SendMessage", user, message)
        } else {
            Log.e("SignalR", "Cannot send message, not connected!")
        }
    }
}
