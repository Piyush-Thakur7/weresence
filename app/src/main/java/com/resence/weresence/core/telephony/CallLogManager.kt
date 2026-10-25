package com.resence.weresence.core.telephony

import android.content.Context
import android.provider.CallLog
import android.util.Log

/**
 * Manages native call logs to ensure 0% leakage into Google Phone or Samsung Phone apps.
 */
object CallLogManager {

    private const val TAG = "CallLogManager"

    /**
     * Immediately scrubs any incoming or outgoing call record for [phoneNumber]
     * from Android's system CallLog content provider.
     */
    fun scrubCallLogForNumber(context: Context, phoneNumber: String) {
        try {
            val contentResolver = context.contentResolver
            val cleanNumber = phoneNumber.replace(" ", "").replace("-", "")

            // Delete matching records
            val deletedRows = contentResolver.delete(
                CallLog.Calls.CONTENT_URI,
                "${CallLog.Calls.NUMBER} LIKE ?",
                arrayOf("%$cleanNumber%")
            )

            Log.d(TAG, "Scrubbed $deletedRows call log entries for number: $cleanNumber")
        } catch (e: SecurityException) {
            Log.w(TAG, "Lacks WRITE_CALL_LOG permission or scrub failed: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Error scrubbing call log: ${e.message}")
        }
    }
}
