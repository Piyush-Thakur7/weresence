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
    RESENCE_AI_ENGINE,
    GEMINI_FLASH_MULTIMODAL,
    GROQ_LLAMA_HIGH_SPEED
}

data class ResenceAiRequest(
    val prompt: String,
    val isVoiceMode: Boolean = false
)

data class ResenceAiResponse(
    val replyText: String,
    val providerUsed: AiModelProvider = AiModelProvider.RESENCE_AI_ENGINE
)

/**
 * Resence AI Engine Client.
 * Connects directly to https://ai.resence.in/api/chat with full SSE streaming parser.
 */
class ResenceAiService(
    private val gatewayUrl: String = "https://ai.resence.in/api/chat"
) {

    companion object {
        private const val TAG = "ResenceAiService"
        const val BOT_HANDLE = "@ResenceAI"
        const val BOT_NAME = "Resence AI"
    }

    suspend fun processQuery(request: ResenceAiRequest, isOnline: Boolean = true): ResenceAiResponse = withContext(Dispatchers.IO) {
        if (!isOnline) {
            return@withContext ResenceAiResponse(
                replyText = "Resence AI requires active data connection."
            )
        }

        try {
            val url = URL(gatewayUrl)
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Accept", "text/event-stream, application/json")
                connectTimeout = 12000
                readTimeout = 25000
                doOutput = true
                doInput = true
            }

            val payload = JSONObject().apply {
                put("prompt", request.prompt)
            }

            OutputStreamWriter(conn.outputStream).use { writer ->
                writer.write(payload.toString())
                writer.flush()
            }

            val responseCode = conn.responseCode
            if (responseCode == 200) {
                val fullResponse = StringBuilder()
                BufferedReader(InputStreamReader(conn.inputStream)).use { reader ->
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        val currentLine = line?.trim() ?: continue
                        if (currentLine.startsWith("data: ")) {
                            val dataStr = currentLine.removePrefix("data: ").trim()
                            if (dataStr == "[DONE]") break
                            try {
                                val chunk = JSONObject(dataStr)
                                val choices = chunk.optJSONArray("choices")
                                if (choices != null && choices.length() > 0) {
                                    val delta = choices.getJSONObject(0).optJSONObject("delta")
                                    val text = delta?.optString("content", "") ?: ""
                                    if (text.isNotEmpty()) {
                                        fullResponse.append(text)
                                    }
                                }
                            } catch (_: Exception) {
                                // Continue streaming
                            }
                        } else if (!currentLine.startsWith(":") && currentLine.isNotEmpty()) {
                            // Non-SSE standard JSON fallback
                            try {
                                val json = JSONObject(currentLine)
                                val text = json.optString("reply", json.optString("text", ""))
                                if (text.isNotEmpty()) {
                                    fullResponse.append(text)
                                }
                            } catch (_: Exception) { }
                        }
                    }
                }

                val finalReply = fullResponse.toString().trim()
                if (finalReply.isNotEmpty()) {
                    return@withContext ResenceAiResponse(replyText = finalReply)
                } else {
                    return@withContext ResenceAiResponse(replyText = "I'm ready. How can I assist you today?")
                }
            } else {
                Log.e(TAG, "Gateway returned HTTP $responseCode")
                return@withContext ResenceAiResponse(
                    replyText = "Resence AI server response: HTTP $responseCode. Please check connectivity."
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception contacting Resence AI gateway", e)
            return@withContext ResenceAiResponse(
                replyText = "Could not connect to Resence AI: ${e.localizedMessage ?: "Network error"}"
            )
        }
    }
}
