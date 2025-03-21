package com.projscope.real_time_communication

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private val signalRService = SignalRService()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val startButton: Button = findViewById(R.id.btn_start)
        val stopButton: Button = findViewById(R.id.btn_stop)

        // Subscribe before any interaction
        signalRService.connectionStatus.subscribe(
            { status ->
                Log.d("SignalR", "status: $status")
                runOnUiThread {
                    startButton.isEnabled = !status
                    stopButton.isEnabled = status
                }
            },
            { error -> Log.e("SignalR", "Subscription Error: ${error.message}") }
        )

        // Button click listeners after subscription
        startButton.setOnClickListener {
            signalRService.startConnection(baseContext)
        }

        stopButton.setOnClickListener {
            signalRService.stopConnection()
        }
    }
}