package com.resence.weresence.core.dispatcher

import android.content.Context
import android.util.Log
import com.resence.weresence.core.crypto.AesCryptoEnvelope
import com.resence.weresence.core.network.ConnectionManager
import com.resence.weresence.core.network.TransportMode
import com.resence.weresence.core.telephony.DataSmsSender
import java.util.UUID
import javax.crypto.SecretKey

/**
 * Central routing brain: Decides whether to send via Internet or fallback to Data-SMS.
 */
class HybridMessageDispatcher(
    private val context: Context,
    private val connectionManager: ConnectionManager,
    private val dataSmsSender: DataSmsSender
) {

    companion object {
        private const val TAG = "HybridDispatcher"
    }

    /**
     * Dispatches [messageText] to [recipientNumber].
     * Auto-detects whether to use WebSocket/HTTPS or Stealth Data SMS.
     */
    fun dispatchMessage(
        recipientNumber: String,
        messageText: String,
        sessionKey: SecretKey,
        onSuccess: (modeUsed: TransportMode, messageId: String) -> Unit,
        onFailure: (error: String) -> Unit
    ) {
        val messageId = UUID.randomUUID().toString()
        val encryptedPayload = AesCryptoEnvelope.encrypt(messageText, sessionKey)

        val activeMode = connectionManager.currentMode

        when (activeMode) {
            TransportMode.INTERNET_HIGH_SPEED -> {
                Log.d(TAG, "Dispatching via INTERNET (Cloud Gateway)")
                // Simulated Cloud WebSocket / HTTP dispatch
                // In production, this calls our WebSocket Client (Socket.IO / Ktor / Retrofit)
                val cloudSuccess = sendViaCloudGateway(recipientNumber, encryptedPayload, messageId)
                if (cloudSuccess) {
                    onSuccess(TransportMode.INTERNET_HIGH_SPEED, messageId)
                } else {
                    Log.w(TAG, "Cloud gateway failed -> Immediate fallback to STEALTH_DATA_SMS")
                    val smsSuccess = dataSmsSender.sendStealthMessage(recipientNumber, encryptedPayload, messageId)
                    if (smsSuccess) {
                        onSuccess(TransportMode.STEALTH_DATA_SMS, messageId)
                    } else {
                        onFailure("Both Internet and Data SMS dispatch failed")
                    }
                }
            }
            TransportMode.STEALTH_DATA_SMS -> {
                Log.d(TAG, "Dispatching via STEALTH_DATA_SMS (Port 8901)")
                val smsSuccess = dataSmsSender.sendStealthMessage(recipientNumber, encryptedPayload, messageId)
                if (smsSuccess) {
                    onSuccess(TransportMode.STEALTH_DATA_SMS, messageId)
                } else {
                    onFailure("Data SMS transmission failed. Check SIM balance.")
                }
            }
        }
    }

    private fun sendViaCloudGateway(recipient: String, payload: ByteArray, msgId: String): Boolean {
        // Placeholder for WebSocket transmission
        return true
    }
}
