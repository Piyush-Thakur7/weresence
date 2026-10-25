package com.resence.stealthchat.core.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log

enum class TransportMode {
    INTERNET_HIGH_SPEED, // WebSockets / HTTPS (Instant, Zero Carrier Cost)
    STEALTH_DATA_SMS     // Port-directed Data SMS (Offline Fallback)
}

/**
 * Monitors live network connectivity and triggers automatic transport switching.
 */
class ConnectionManager(context: Context) {

    companion object {
        private const val TAG = "ConnectionManager"
    }

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    var currentMode: TransportMode = TransportMode.STEALTH_DATA_SMS
        private set

    var onTransportModeChanged: ((newMode: TransportMode) -> Unit)? = null

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            Log.d(TAG, "Network restored -> Switching to INTERNET_HIGH_SPEED")
            currentMode = TransportMode.INTERNET_HIGH_SPEED
            onTransportModeChanged?.invoke(currentMode)
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            Log.d(TAG, "Network dropped -> Auto-falling back to STEALTH_DATA_SMS")
            currentMode = TransportMode.STEALTH_DATA_SMS
            onTransportModeChanged?.invoke(currentMode)
        }
    }

    fun startMonitoring() {
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(request, networkCallback)

        // Initial check
        val activeNetwork = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        val hasInternet = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true

        currentMode = if (hasInternet) TransportMode.INTERNET_HIGH_SPEED else TransportMode.STEALTH_DATA_SMS
        onTransportModeChanged?.invoke(currentMode)
    }

    fun stopMonitoring() {
        try {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to unregister network callback: ${e.message}")
        }
    }
}
