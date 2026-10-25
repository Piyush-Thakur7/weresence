package com.resence.weresence.core.media

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

enum class MediaType {
    TEXT,
    PHOTO,
    VIDEO,
    AUDIO_VOICE_NOTE,
    DOCUMENT
}

enum class MediaTransferState {
    UPLOADED_TO_CLOUD,
    STREAMED_VIA_CHUNKS,
    QUEUED_OFFLINE // Will auto-sync when internet returns
}

data class MediaAttachment(
    val mediaId: String,
    val type: MediaType,
    val localUri: String,
    val remoteCloudUrl: String? = null,
    val thumbnailBase64: String? = null,
    val fileSizeBytes: Long = 0,
    val durationSeconds: Int = 0, // For audio/video
    val transferState: MediaTransferState = MediaTransferState.QUEUED_OFFLINE
)

/**
 * Handles compression, thumbnailing, and encoding for Photos, Videos, and Voice Notes.
 */
class MediaManager(private val context: Context) {

    companion object {
        private const val TAG = "MediaManager"
        private const val MAX_IMAGE_DIMENSION = 1280
        private const val THUMBNAIL_DIMENSION = 64 // Ultra-low size for offline metadata preview
    }

    /**
     * Compresses an image and generates a tiny micro-thumbnail.
     */
    fun processImage(imageUri: Uri): Pair<File, String> {
        val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
        val originalBitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        // 1. Generate full compressed image for cloud upload
        val compressedFile = File(context.cacheDir, "img_${System.currentTimeMillis()}.webp")
        val outputStream = FileOutputStream(compressedFile)
        originalBitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, 75, outputStream)
        outputStream.flush()
        outputStream.close()

        // 2. Generate micro-thumbnail (fits in a tiny ~400-byte envelope)
        val thumbBitmap = Bitmap.createScaledBitmap(originalBitmap, THUMBNAIL_DIMENSION, THUMBNAIL_DIMENSION, true)
        val thumbStream = ByteArrayOutputStream()
        thumbBitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, 30, thumbStream)
        val thumbBytes = thumbStream.toByteArray()
        val thumbBase64 = Base64.encodeToString(thumbBytes, Base64.NO_WRAP)

        Log.d(TAG, "Processed photo: Full=${compressedFile.length()} bytes, MicroThumb=${thumbBytes.size} bytes")
        return Pair(compressedFile, thumbBase64)
    }

    /**
     * Prepares a voice recording file path.
     */
    fun createVoiceNoteFile(): File {
        return File(context.filesDir, "voice_${System.currentTimeMillis()}.m4a")
    }
}
