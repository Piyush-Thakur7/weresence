package com.resence.weresence.core.ai

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

enum class AiModelProvider {
    GEMINI_FLASH_MULTIMODAL, // Google Gemini 2.5 Flash (Vision + Reasoning)
    GROQ_LLAMA_HIGH_SPEED,   // Groq Meta Llama 3.3 70B (500 t/s real-time voice & text)
    DEEPSEEK_R1_REASONING    // DeepSeek-R1 (Complex STEM & logic)
}

data class ResenceAiRequest(
    val prompt: String,
    val base64Image: String? = null,
    val conversationHistory: List<Map<String, String>> = emptyList(),
    val isVoiceMode: Boolean = false
)

data class ResenceAiResponse(
    val replyText: String,
    val audioUrl: String? = null,
    val tokensUsed: Int = 0,
    val providerUsed: AiModelProvider = AiModelProvider.GEMINI_FLASH_MULTIMODAL
)

class ResenceAiService(
    private val gatewayUrl: String = "https://ai.resence.in/api/chat",
    private val userSessionToken: String = ""
) {

    companion object {
        private const val TAG = "ResenceAiService"
        const val BOT_HANDLE = "@ResenceAI"
        const val BOT_NAME = "Resence AI"
        const val OFFLINE_MESSAGE = "Resence AI requires active data to think. Offline SMS is reserved exclusively for sovereign 1-on-1 human communication."
    }

    suspend fun processQuery(request: ResenceAiRequest, isOnline: Boolean): ResenceAiResponse = withContext(Dispatchers.IO) {
        if (!isOnline) {
            Log.d(TAG, "Device is offline. Freezing AI companion to preserve cellular SMS quotas.")
            return@withContext ResenceAiResponse(
                replyText = OFFLINE_MESSAGE,
                providerUsed = AiModelProvider.GEMINI_FLASH_MULTIMODAL
            )
        }

        try {
            val url = URL(gatewayUrl)
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Authorization", "Bearer $userSessionToken")
                connectTimeout = 8000
                readTimeout = 15000
                doOutput = true
                doInput = true
            }

            val payload = JSONObject().apply {
                put("prompt", request.prompt)
                put("hasImage", request.base64Image != null)
                request.base64Image?.let { put("image", it) }
                put("isVoice", request.isVoiceMode)
            }

            OutputStreamWriter(conn.outputStream).use { writer ->
                writer.write(payload.toString())
                writer.flush()
            }

            val responseCode = conn.responseCode
            if (responseCode == 200) {
                val responseString = BufferedReader(InputStreamReader(conn.inputStream)).use { it.readText() }
                val json = JSONObject(responseString)
                val reply = json.optString("reply", "No response received.")
                val model = if (request.base64Image != null) AiModelProvider.GEMINI_FLASH_MULTIMODAL else AiModelProvider.GROQ_LLAMA_HIGH_SPEED
                return@withContext ResenceAiResponse(
                    replyText = reply,
                    providerUsed = model
                )
            } else {
                return@withContext ResenceAiResponse(
                    replyText = "Resence AI is currently synchronizing with the neural gateway.",
                    providerUsed = AiModelProvider.GEMINI_FLASH_MULTIMODAL
                )
            }
        } catch (e: Exception) {
            return@withContext ResenceAiResponse(
                replyText = "Resence AI: Connection error (${e.localizedMessage ?: "Network error"}). Please verify connectivity.",
                providerUsed = AiModelProvider.GEMINI_FLASH_MULTIMODAL
            )
        }
    }
}
