package com.resence.weresence

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.resence.weresence.core.network.ConnectionManager
import com.resence.weresence.core.network.TransportMode

class MainActivity : AppCompatActivity() {

    private lateinit var connectionManager: ConnectionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Enforce Anti-Screenshot & Anti-Screen Recording Across Entire App
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        // 2. Set Up Clean Activity View
        setContentView(R.layout.activity_main)

        // 3. Start Live Network Monitoring (Auto-shift Online <-> Offline SMS)
        connectionManager = ConnectionManager(this)
        connectionManager.onTransportModeChanged = { mode ->
            runOnUiThread {
                when (mode) {
                    TransportMode.INTERNET_HIGH_SPEED -> {
                        // Online mode: WebSockets & rich media active
                    }
                    TransportMode.STEALTH_DATA_SMS -> {
                        // Offline mode: Stealth SMS active on Port 8901
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
