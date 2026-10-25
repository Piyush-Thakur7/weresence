package com.resence.weresence

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.resence.weresence.core.ai.ResenceAiRequest
import com.resence.weresence.core.ai.ResenceAiService
import com.resence.weresence.core.auth.SupabaseAuthService
import com.resence.weresence.core.network.ConnectionManager
import com.resence.weresence.core.network.TransportMode
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var connectionManager: ConnectionManager
    private val supabaseAuthService = SupabaseAuthService()
    private val resenceAiService = ResenceAiService()

    private var isOnline = true

    override fun onCreate(savedInstanceState: Bundle?) {
        // Enforce Strict OLED Dark Theme
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        super.onCreate(savedInstanceState)

        // Enforce Anti-Screenshot across entire app
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        setContentView(R.layout.activity_main)

        setupHeader()
        setupCamouflageTest()
        setupSupabaseAuth()
        setupAiCompanion()
        setupNetworkMonitoring()
    }

    private fun setupHeader() {
        findViewById<TextView>(R.id.btnLockVault).setOnClickListener {
            val intent = Intent(this, PinLockActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }
    }

    private fun setupCamouflageTest() {
        findViewById<Button>(R.id.btnTestCamouflage).setOnClickListener {
            val intent = Intent(this, DecoyAiActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupSupabaseAuth() {
        val etPhoneNumber = findViewById<EditText>(R.id.etPhoneNumber)
        val btnSendOtp = findViewById<Button>(R.id.btnSendOtp)
        val layoutOtpVerify = findViewById<LinearLayout>(R.id.layoutOtpVerify)
        val etOtpCode = findViewById<EditText>(R.id.etOtpCode)
        val btnVerifyOtp = findViewById<Button>(R.id.btnVerifyOtp)
        val tvAuthResult = findViewById<TextView>(R.id.tvAuthResult)

        btnSendOtp.setOnClickListener {
            val phone = etPhoneNumber.text.toString().trim()
            if (phone.isEmpty()) {
                tvAuthResult.text = "Please enter a valid phone number with country code (e.g. +91...)"
                return@setOnClickListener
            }

            btnSendOtp.isEnabled = false
            btnSendOtp.text = "Sending OTP..."
            tvAuthResult.text = ""

            lifecycleScope.launch {
                val result = supabaseAuthService.sendOtp(phone)
                btnSendOtp.isEnabled = true
                btnSendOtp.text = "Request OTP"

                result.fold(
                    onSuccess = {
                        layoutOtpVerify.visibility = View.VISIBLE
                        tvAuthResult.text = "✅ OTP requested! Enter code below (or use test OTP if configured in Supabase)."
                    },
                    onFailure = { error ->
                        tvAuthResult.text = "❌ Error: ${error.message}"
                    }
                )
            }
        }

        btnVerifyOtp.setOnClickListener {
            val phone = etPhoneNumber.text.toString().trim()
            val otp = etOtpCode.text.toString().trim()
            if (otp.isEmpty()) {
                tvAuthResult.text = "Please enter the 6-digit OTP"
                return@setOnClickListener
            }

            btnVerifyOtp.isEnabled = false
            btnVerifyOtp.text = "Verifying..."

            lifecycleScope.launch {
                val result = supabaseAuthService.verifyOtp(phone, otp)
                btnVerifyOtp.isEnabled = true
                btnVerifyOtp.text = "Verify & Create Sovereign Session"

                result.fold(
                    onSuccess = { session ->
                        tvAuthResult.text = "✅ Sovereign session established! User ID: ${session.userId.take(8)}..."
                    },
                    onFailure = { error ->
                        tvAuthResult.text = "❌ Verification failed: ${error.message}"
                    }
                )
            }
        }
    }

    private fun setupAiCompanion() {
        val etPrompt = findViewById<EditText>(R.id.etAiPrompt)
        val btnAskAi = findViewById<Button>(R.id.btnAskAi)
        val tvResponse = findViewById<TextView>(R.id.tvAiResponse)

        btnAskAi.setOnClickListener {
            val query = etPrompt.text.toString().trim()
            if (query.isEmpty()) return@setOnClickListener

            btnAskAi.isEnabled = false
            btnAskAi.text = "Thinking..."
            tvResponse.text = "Connecting to Resence AI..."

            lifecycleScope.launch {
                val response = resenceAiService.processQuery(
                    ResenceAiRequest(prompt = query),
                    isOnline = isOnline
                )
                btnAskAi.isEnabled = true
                btnAskAi.text = "Ask AI Companion"
                tvResponse.text = response.replyText
            }
        }
    }

    private fun setupNetworkMonitoring() {
        val tvNetworkStatus = findViewById<TextView>(R.id.tvNetworkStatus)

        connectionManager = ConnectionManager(this)
        connectionManager.onTransportModeChanged = { mode ->
            runOnUiThread {
                when (mode) {
                    TransportMode.INTERNET_HIGH_SPEED -> {
                        isOnline = true
                        tvNetworkStatus.text = "🟢 ONLINE"
                    }
                    TransportMode.STEALTH_DATA_SMS -> {
                        isOnline = false
                        tvNetworkStatus.text = "📶 STEALTH SMS"
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        connectionManager.startMonitoring()
    }

    override fun onPause() {
        super.onPause()
        connectionManager.stopMonitoring()
    }
}
