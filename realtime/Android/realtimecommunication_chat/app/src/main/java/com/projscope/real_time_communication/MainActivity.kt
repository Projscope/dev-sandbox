package com.projscope.real_time_communication

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private val signalRService = SignalRService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val startButton: Button = findViewById(R.id.btn_start)
        val stopButton: Button = findViewById(R.id.btn_stop)
        val sendButton: Button = findViewById(R.id.btn_send)
        val messageInput: EditText = findViewById(R.id.et_message)
        val chatLayout: LinearLayout = findViewById(R.id.chat_layout)
        val scrollView: ScrollView = findViewById(R.id.scroll_view)

        signalRService.connectionStatus.subscribe(
            { status ->
                Log.d("SignalR", "status: $status")
                startButton.isEnabled = !status
                sendButton.isEnabled = !status

                stopButton.isEnabled = status
            },
            { error -> Log.e("SignalR", "Error: ${error.message}") }
        )

        signalRService.receivedMessages.subscribe { message ->
            runOnUiThread {
                addMessageToChat(message, false, chatLayout, scrollView)
            }
        }

        startButton.setOnClickListener {
            signalRService.startConnection(baseContext)
        }

        stopButton.setOnClickListener {
            signalRService.stopConnection()
        }

        sendButton.setOnClickListener {
            val message = messageInput.text.toString()
            if (message.isNotEmpty()) {
                signalRService.sendMessage("User", message)
                addMessageToChat(message, true, chatLayout, scrollView)
                messageInput.setText("")
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun addMessageToChat(
        message: String,
        isSent: Boolean,
        chatLayout: LinearLayout,
        scrollView: ScrollView
    ) {

        var parsedMessage = message

        if (!isSent){
            // Parse the message to extract the "data" field (actual message text)
            parsedMessage = try {
                val jsonObject = JSONObject(message)
                jsonObject.optString("data", "No message data available")
            } catch (e: Exception) {

                message + " --> Error parsing message"
            }
        }


        val textView = TextView(this).apply {
            text = parsedMessage
            textSize = 16f
            setPadding(12, 8, 12, 8)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = if (isSent) Gravity.END else Gravity.START
            }

            // Create a GradientDrawable for rounded corners and background color
            val drawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 30f  // Adjust the corner radius as needed
                setColor(if (isSent) Color.parseColor("#A2D2FF") else Color.parseColor("#BDE0FE"))
            }

            // Set the background to the GradientDrawable
            background = drawable

            setTextColor(Color.parseColor("#2A9D8F"))
        }
        chatLayout.addView(textView)
        scrollView.post { scrollView.fullScroll(ScrollView.FOCUS_DOWN) }
    }
}
