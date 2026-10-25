package com.resence.stealthchat.core.telephony

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import android.util.Log

/**
 * DataSmsReceiver intercepts binary SMS arriving on Port 8901.
 * Standard SMS applications (Google Messages, Samsung Messages) IGNORE this traffic.
 */
class DataSmsReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "DataSmsReceiver"
    }

    // Callback hook for the application layer to consume incoming binary messages
    var onDataMessageReceived: ((sender: String, data: ByteArray) -> Unit)? = null

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == "android.intent.action.DATA_SMS_RECEIVED") {
            val uri = intent.data
            val port = uri?.port
            Log.d(TAG, "Data SMS received on port: $port")

            val bundle = intent.extras
            if (bundle != null) {
                val pdus = bundle.get("pdus") as? Array<*>
                if (pdus != null) {
                    for (pdu in pdus) {
                        val format = bundle.getString("format")
                        val message = if (format != null) {
                            SmsMessage.createFromPdu(pdu as ByteArray, format)
                        } else {
                            @Suppress("DEPRECATION")
                            SmsMessage.createFromPdu(pdu as ByteArray)
                        }

                        val sender = message.displayOriginatingAddress ?: "Unknown"
                        val userData = message.userData // The raw binary payload

                        if (userData != null && userData.isNotEmpty()) {
                            Log.d(TAG, "Intercepted ${userData.size} bytes from $sender. Routing to StealthChat decryptor...")
                            onDataMessageReceived?.invoke(sender, userData)
                        }
                    }
                }
            }
        }
    }
}
