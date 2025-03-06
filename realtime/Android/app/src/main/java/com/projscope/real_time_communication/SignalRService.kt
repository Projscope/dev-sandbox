package com.projscope.real_time_communication

import android.annotation.SuppressLint
import android.util.Log
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder

class SignalRService {
    private var hubConnection: HubConnection? = null

    @SuppressLint("CheckResult")
    fun startConnection() {
        hubConnection = HubConnectionBuilder.create("wss://......./com-hub")
            .build()

        // Start the connection
        hubConnection?.start()?.subscribe({
            Log.d("SignalR", "Connected successfully!")

            // Join Group
            hubConnection?.invoke("JoinGroup", "api_\\|^T8.%1n5", "channel_DGDrOB7OfYUJ")
            Log.d("SignalR", "Joining group")

        }, { error ->
            Log.e("SignalR", "Error connecting: ${error.message}")
        })


        // Listen for messages from the server
        hubConnection?.on("ReceiveMessage", { message: String ->
            Log.d("SignalR", "Received: $message")
        }, String::class.java)
    }

    fun stopConnection() {
        hubConnection?.stop()
    }

    fun sendMessage(user: String, message: String) {
        hubConnection?.invoke("SendMessage", user, message)
    }
}
