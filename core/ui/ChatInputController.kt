package com.resence.stealthchat.core.ui

import com.resence.stealthchat.core.network.ConnectionManager
import com.resence.stealthchat.core.network.TransportMode

data class ChatInputBarUiState(
    val isTextChatEnabled: Boolean = true,
    val isPhotoSharingEnabled: Boolean = true,
    val isVideoSharingEnabled: Boolean = true,
    val isVoiceNoteEnabled: Boolean = true,
    val transportBadgeText: String = "Online: Full Multimedia",
    val transportBadgeColorHex: String = "#00C853", // Green
    val mediaLockedTooltip: String? = null
)

/**
 * Dynamically controls the chat bar based on real-time internet availability.
 * Rule: 
 * - Online  -> ALL multimedia enabled (Photos, Videos, Voice Notes, Text).
 * - Offline -> Media options LOCK 🔒, ONLY Text Chat remains active via Stealth SMS.
 */
class ChatInputController(private val connectionManager: ConnectionManager) {

    var onUiStateChanged: ((ChatInputBarUiState) -> Unit)? = null

    fun initialize() {
        connectionManager.onTransportModeChanged = { mode ->
            val newState = when (mode) {
                TransportMode.INTERNET_HIGH_SPEED -> {
                    ChatInputBarUiState(
                        isTextChatEnabled = true,
                        isPhotoSharingEnabled = true,
                        isVideoSharingEnabled = true,
                        isVoiceNoteEnabled = true,
                        transportBadgeText = "🟢 Online (WhatsApp Mode)",
                        transportBadgeColorHex = "#00C853",
                        mediaLockedTooltip = null
                    )
                }
                TransportMode.STEALTH_DATA_SMS -> {
                    ChatInputBarUiState(
                        isTextChatEnabled = true,
                        isPhotoSharingEnabled = false, // LOCKED 🔒
                        isVideoSharingEnabled = false, // LOCKED 🔒
                        isVoiceNoteEnabled = false,    // LOCKED 🔒
                        transportBadgeText = "⚡ Offline (Stealth SMS Mode)",
                        transportBadgeColorHex = "#FF9100", // Amber
                        mediaLockedTooltip = "Photos and videos require internet. Text chat is active via Stealth SMS."
                    )
                }
            }
            onUiStateChanged?.invoke(newState)
        }
    }
}
