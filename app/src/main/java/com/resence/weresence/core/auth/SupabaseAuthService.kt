package com.resence.weresence.core.auth

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Sovereign Supabase Phone Authentication Service.
 *
 * Direct REST implementation over OkHttp:
 * - Clean, zero-conflict architecture (no heavy multiplatform runtime needed).
 * - Implements Phone OTP Request & Verify against project `zmpydrioaaofgtoweai`.
 * - Uses publishable anon key. (Service role secret is strictly kept out of client binaries).
 */
class SupabaseAuthService(
    private val supabaseUrl: String = "https://zmpydrioaaoafgtoweai.supabase.co",
    private val supabaseAnonKey: String = "sb_publishable_AZecz4QWSu-XIO7_ac5sHA_YH7AAPJD"
) {

    companion object {
        private const val TAG = "SupabaseAuthService"
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    /**
     * Send Phone OTP.
     * @param phoneNumber E.164 formatted (e.g. +919876543210)
     */
    suspend fun sendOtp(phoneNumber: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val payload = JSONObject().apply {
                put("phone", phoneNumber)
            }

            val request = Request.Builder()
                .url("$supabaseUrl/auth/v1/otp")
                .addHeader("apikey", supabaseAnonKey)
                .addHeader("Content-Type", "application/json")
                .post(payload.toString().toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: ""

            if (response.isSuccessful) {
                Log.i(TAG, "OTP successfully requested for $phoneNumber")
                Result.success(body)
            } else {
                Log.e(TAG, "OTP request failed: HTTP ${response.code} $body")
                Result.failure(Exception("Supabase OTP failed: HTTP ${response.code} $body"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Network exception requesting OTP", e)
            Result.failure(e)
        }
    }

    /**
     * Verify Phone OTP.
     * @param phoneNumber E.164 formatted
     * @param otpToken 6-digit verification code
     */
    suspend fun verifyOtp(phoneNumber: String, otpToken: String): Result<SupabaseSession> = withContext(Dispatchers.IO) {
        try {
            val payload = JSONObject().apply {
                put("type", "sms")
                put("phone", phoneNumber)
                put("token", otpToken)
            }

            val request = Request.Builder()
                .url("$supabaseUrl/auth/v1/verify")
                .addHeader("apikey", supabaseAnonKey)
                .addHeader("Content-Type", "application/json")
                .post(payload.toString().toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: ""

            if (response.isSuccessful) {
                val json = JSONObject(body)
                val accessToken = json.optString("access_token")
                val refreshToken = json.optString("refresh_token")
                val userObj = json.optJSONObject("user")
                val userId = userObj?.optString("id") ?: ""

                Log.i(TAG, "OTP verification successful for user $userId")
                Result.success(SupabaseSession(accessToken, refreshToken, userId, phoneNumber))
            } else {
                Log.e(TAG, "OTP verification failed: HTTP ${response.code} $body")
                Result.failure(Exception("Supabase Verify failed: HTTP ${response.code} $body"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Network exception verifying OTP", e)
            Result.failure(e)
        }
    }
}

data class SupabaseSession(
    val accessToken: String,
    val refreshToken: String,
    val userId: String,
    val phoneNumber: String
)
