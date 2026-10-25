package com.resence.weresence.core.ai

import com.resence.weresence.core.network.ConnectionManager
import com.resence.weresence.core.network.TransportMode

data class ResenceAiUiState(
    val isAiActive: Boolean = true,
    val avatarGlowColorHex: String = "#7C4DFF", // Glowing Neon Purple (Online)
    val inputPlaceholder: String = "Ask Resence AI anything...",
    val isInputEnabled: Boolean = true,
    val offlineBannerMessage: String? = null
)

/**
 * Controller enforcing the Strict AI Rule:
 * - Online: AI is vibrant, active, and glowing.
 * - Offline: AI turns GREY, disabled. NO offline queueing. Blocked until internet returns.
 */
class ResenceAiUiController(private val connectionManager: ConnectionManager) {

    var onAiStateChanged: ((ResenceAiUiState) -> Unit)? = null

    fun initialize() {
        connectionManager.onTransportModeChanged = { mode ->
            val newState = when (mode) {
                TransportMode.INTERNET_HIGH_SPEED -> {
                    ResenceAiUiState(
                        isAiActive = true,
                        avatarGlowColorHex = "#7C4DFF", // Active Glowing Neon
                        inputPlaceholder = "Ask Resence AI anything...",
                        isInputEnabled = true,
                        offlineBannerMessage = null
                    )
                }
                TransportMode.STEALTH_DATA_SMS -> {
                    // STRICT RULE: Turn Grey, Disable Input, Zero Queueing
                    ResenceAiUiState(
                        isAiActive = false,
                        avatarGlowColorHex = "#757575", // Muted Muted Grey
                        inputPlaceholder = "Resence AI is offline. Connect to internet to chat.",
                        isInputEnabled = false,
                        offlineBannerMessage = "⚠️ Resence AI requires an active internet connection."
                    )
                }
            }
            onAiStateChanged?.invoke(newState)
        }
    }
}
