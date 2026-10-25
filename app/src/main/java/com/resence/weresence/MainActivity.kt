package com.resence.weresence

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.resence.weresence.core.auth.SupabaseAuthService
import com.resence.weresence.core.network.ConnectionManager
import com.resence.weresence.core.network.TransportMode
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var connectionManager: ConnectionManager
    private val supabaseAuthService = SupabaseAuthService()

    private lateinit var viewChatsTab: ScrollView
    private lateinit var viewVaultTab: LinearLayout
    private lateinit var viewSettingsTab: ScrollView

    private lateinit var tabChats: TextView
    private lateinit var tabVault: TextView
    private lateinit var tabSettings: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        super.onCreate(savedInstanceState)

        // NOTE: Screenshots are PERMITTED on main navigation screen as per user request.
        // FLAG_SECURE is strictly enforced inside 1-on-1 ChatActivity and PrivateGalleryActivity!
        setContentView(R.layout.activity_main)

        initViews()
        setupNavigationTabs()
        setupChatNavigation()
        setupSupabaseAuth()
        setupHeader()
        setupNetworkMonitoring()
    }

    private fun initViews() {
        viewChatsTab = findViewById(R.id.viewChatsTab)
        viewVaultTab = findViewById(R.id.viewVaultTab)
        viewSettingsTab = findViewById(R.id.viewSettingsTab)

        tabChats = findViewById(R.id.tabChats)
        tabVault = findViewById(R.id.tabVault)
        tabSettings = findViewById(R.id.tabSettings)
    }

    private fun setupNavigationTabs() {
        tabChats.setOnClickListener { switchTab(0) }
        tabVault.setOnClickListener { switchTab(1) }
        tabSettings.setOnClickListener { switchTab(2) }

        findViewById<Button>(R.id.btnOpenPrivateGallery).setOnClickListener {
            val intent = Intent(this, PrivateGalleryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun switchTab(index: Int) {
        viewChatsTab.visibility = if (index == 0) View.VISIBLE else View.GONE
        viewVaultTab.visibility = if (index == 1) View.VISIBLE else View.GONE
        viewSettingsTab.visibility = if (index == 2) View.VISIBLE else View.GONE

        val activeColor = getColor(R.color.emerald_400)
        val inactiveColor = getColor(R.color.slate_400)

        tabChats.setTextColor(if (index == 0) activeColor else inactiveColor)
        tabVault.setTextColor(if (index == 1) activeColor else inactiveColor)
        tabSettings.setTextColor(if (index == 2) activeColor else inactiveColor)
    }

    private fun setupChatNavigation() {
        // Chat 1: Resence AI Companion (Connected to ai.resence.in with SSE streaming)
        val openAiChat = {
            val intent = Intent(this, ChatActivity::class.java).apply {
                putExtra(ChatActivity.EXTRA_PEER_NAME, "Resence AI")
                putExtra(ChatActivity.EXTRA_IS_AI, true)
            }
            startActivity(intent)
        }
        findViewById<View>(R.id.chatItemResenceAi).setOnClickListener { openAiChat() }
        findViewById<View>(R.id.storyResenceAi).setOnClickListener { openAiChat() }

        // Chat 2: Sovereign Peer Channel
        val openPeerChat = {
            val intent = Intent(this, ChatActivity::class.java).apply {
                putExtra(ChatActivity.EXTRA_PEER_NAME, "Piyush Singh (Founder)")
                putExtra(ChatActivity.EXTRA_IS_AI, false)
            }
            startActivity(intent)
        }
        findViewById<View>(R.id.chatItemPiyush).setOnClickListener { openPeerChat() }
        findViewById<View>(R.id.storyPiyush).setOnClickListener { openPeerChat() }

        // Story 3: Vault Shortcut
        findViewById<View>(R.id.storyVault).setOnClickListener {
            val intent = Intent(this, PrivateGalleryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupHeader() {
        findViewById<TextView>(R.id.btnLockVault).setOnClickListener {
            val intent = Intent(this, PinLockActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }

        findViewById<Button>(R.id.btnTestCamouflage).setOnClickListener {
            val intent = Intent(this, DecoyAiActivity::class.java)
            startActivity(intent)
        }

        // Open Sovereign Dual Authentication Screen (Google + Phone)
        findViewById<View>(R.id.btnOpenOnboarding)?.setOnClickListener {
            val intent = Intent(this, AuthActivity::class.java)
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
                tvAuthResult.text = "Please enter phone with country code (e.g. +919557740764)"
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
                        tvAuthResult.text = "✅ OTP requested! Enter code below."
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

    private fun setupNetworkMonitoring() {
        val tvNetworkStatus = findViewById<TextView>(R.id.tvNetworkStatus)

        connectionManager = ConnectionManager(this)
        connectionManager.onTransportModeChanged = { mode ->
            runOnUiThread {
                when (mode) {
                    TransportMode.INTERNET_HIGH_SPEED -> {
                        tvNetworkStatus.text = "🟢 ONLINE"
                    }
                    TransportMode.STEALTH_DATA_SMS -> {
                        tvNetworkStatus.text = "📶 STEALTH"
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
