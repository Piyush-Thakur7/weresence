package com.resence.stealthchat.core.telephony

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.SmsManager
import android.util.Log

/**
 * DataSmsSender handles dispatching binary SMS directly to a designated port.
 * It bypasses the standard SMS inbox (Port 0) completely.
 */
class DataSmsSender(private val context: Context) {

    companion object {
        private const val TAG = "DataSmsSender"
        // Designated high port for stealth data communication
        const val STEALTH_PORT: Short = 8901
        const val ACTION_SMS_SENT = "com.resence.stealthchat.SMS_SENT"
        const val ACTION_SMS_DELIVERED = "com.resence.stealthchat.SMS_DELIVERED"
    }

    private val smsManager: SmsManager by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(SmsManager::class.java)
        } else {
            @Suppress("DEPRECATION")
            SmsManager.getDefault()
        }
    }

    /**
     * Sends an encrypted binary payload directly to [destinationNumber] on [STEALTH_PORT].
     */
    fun sendStealthMessage(
        destinationNumber: String,
        encryptedPayload: ByteArray,
        messageId: String
    ): Boolean {
        return try {
            val sentIntent = PendingIntent.getBroadcast(
                context,
                messageId.hashCode(),
                Intent(ACTION_SMS_SENT).putExtra("message_id", messageId),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val deliveryIntent = PendingIntent.getBroadcast(
                context,
                messageId.hashCode(),
                Intent(ACTION_SMS_DELIVERED).putExtra("message_id", messageId),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            Log.d(TAG, "Dispatching ${encryptedPayload.size} bytes to $destinationNumber on Port $STEALTH_PORT")

            // Core Android Telephony API: Sends raw binary data to designated destination port
            smsManager.sendDataMessage(
                destinationNumber,
                null,
                STEALTH_PORT,
                encryptedPayload,
                sentIntent,
                deliveryIntent
            )
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send Data SMS: ${e.message}", e)
            false
        }
    }
}
