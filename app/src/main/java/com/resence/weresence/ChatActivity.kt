package com.resence.weresence

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.resence.weresence.core.ai.ResenceAiRequest
import com.resence.weresence.core.ai.ResenceAiService
import kotlinx.coroutines.launch

class ChatActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_PEER_NAME = "extra_peer_name"
        const val EXTRA_IS_AI = "extra_is_ai"
    }

    private val resenceAiService = ResenceAiService()
    private lateinit var messagesContainer: LinearLayout
    private lateinit var messagesScrollView: ScrollView
    private lateinit var etMessageInput: EditText
    private lateinit var tvTypingIndicator: TextView

    private var isAiChat = true
    private var peerName = "Resence AI"

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        super.onCreate(savedInstanceState)

        // STRICT ENFORCEMENT: Screenshots & Screen Recording prohibited in 1-on-1 Chat
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        setContentView(R.layout.activity_chat)

        peerName = intent.getStringExtra(EXTRA_PEER_NAME) ?: "Resence AI"
        isAiChat = intent.getBooleanExtra(EXTRA_IS_AI, true)

        messagesContainer = findViewById(R.id.messagesContainer)
        messagesScrollView = findViewById(R.id.messagesScrollView)
        etMessageInput = findViewById(R.id.etMessageInput)
        tvTypingIndicator = findViewById(R.id.tvTypingIndicator)

        findViewById<TextView>(R.id.tvChatTitle).text = peerName
        findViewById<TextView>(R.id.tvChatAvatar).text = if (isAiChat) "🤖" else "🔒"

        findViewById<TextView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        findViewById<TextView>(R.id.btnSendMessage).setOnClickListener {
            sendMessage()
        }

        // Add initial greeting
        if (isAiChat) {
            addIncomingMessage("Greetings! I am Resence AI, sovereign intelligence companion connected to ai.resence.in. How can I assist you today?")
        } else {
            addIncomingMessage("Sovereign encrypted channel active. Bit-for-bit lossless media enabled.")
        }
    }

    private fun sendMessage() {
        val text = etMessageInput.text.toString().trim()
        if (text.isEmpty()) return

        addOutgoingMessage(text)
        etMessageInput.setText("")
        scrollToBottom()

        if (isAiChat) {
            tvTypingIndicator.visibility = View.VISIBLE
            lifecycleScope.launch {
                val response = resenceAiService.processQuery(
                    ResenceAiRequest(prompt = text),
                    isOnline = true
                )
                tvTypingIndicator.visibility = View.GONE
                addIncomingMessage(response.replyText)
                vibrateLight()
                scrollToBottom()
            }
        }
    }

    private fun addOutgoingMessage(text: String) {
        val bubble = TextView(this).apply {
            this.text = text
            this.setTextColor(getColor(R.color.white))
            this.textSize = 14f
            this.setBackgroundResource(R.drawable.bg_msg_outgoing)
            this.setPadding(36, 24, 36, 24)
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.END
                topMargin = 12
                bottomMargin = 12
                marginStart = 80
            }
            this.layoutParams = lp
        }
        messagesContainer.addView(bubble)
    }

    private fun addIncomingMessage(text: String) {
        val bubble = TextView(this).apply {
            this.text = text
            this.setTextColor(getColor(R.color.white))
            this.textSize = 14f
            this.setBackgroundResource(R.drawable.bg_msg_incoming)
            this.setPadding(36, 24, 36, 24)
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.START
                topMargin = 12
                bottomMargin = 12
                marginEnd = 80
            }
            this.layoutParams = lp
        }
        messagesContainer.addView(bubble)
    }

    private fun scrollToBottom() {
        messagesScrollView.post {
            messagesScrollView.fullScroll(ScrollView.FOCUS_DOWN)
        }
    }

    private fun vibrateLight() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator?.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                val v = getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                @Suppress("DEPRECATION")
                v?.vibrate(40)
            }
        } catch (_: Exception) { }
    }
}
