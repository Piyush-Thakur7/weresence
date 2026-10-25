package com.resence.weresence.core.crypto

import java.nio.ByteBuffer
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Military-grade AES-256-GCM authenticated encryption.
 * Encrypts cleartext into binary bytes suitable for Data-SMS and WebSocket transmission.
 */
object AesCryptoEnvelope {

    private const val ALGORITHM = "AES/GCM/NoPadding"
    private const val TAG_LENGTH_BITS = 128
    private const val IV_LENGTH_BYTES = 12

    fun generateSecretKey(): SecretKey {
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(256)
        return keyGen.generateKey()
    }

    /**
     * Encrypts [plainText] using [secretKey].
     * Returns: [12-byte IV] + [Ciphertext + 16-byte Auth Tag]
     */
    fun encrypt(plainText: String, secretKey: SecretKey): ByteArray {
        val iv = ByteArray(IV_LENGTH_BYTES).also { SecureRandom().nextBytes(it) }
        val cipher = Cipher.getInstance(ALGORITHM)
        val spec = GCMParameterSpec(TAG_LENGTH_BITS, iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec)

        val cipherBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))

        // Prepend IV to ciphertext
        val byteBuffer = ByteBuffer.allocate(iv.size + cipherBytes.size)
        byteBuffer.put(iv)
        byteBuffer.put(cipherBytes)
        return byteBuffer.array()
    }

    /**
     * Decrypts [encryptedData] using [secretKey].
     */
    fun decrypt(encryptedData: ByteArray, secretKey: SecretKey): String {
        require(encryptedData.size > IV_LENGTH_BYTES) { "Invalid payload length" }

        val byteBuffer = ByteBuffer.wrap(encryptedData)
        val iv = ByteArray(IV_LENGTH_BYTES).also { byteBuffer.get(it) }
        val cipherBytes = ByteArray(byteBuffer.remaining()).also { byteBuffer.get(it) }

        val cipher = Cipher.getInstance(ALGORITHM)
        val spec = GCMParameterSpec(TAG_LENGTH_BITS, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)

        val plainBytes = cipher.doFinal(cipherBytes)
        return String(plainBytes, Charsets.UTF_8)
    }

    fun keyFromBytes(rawKey: ByteArray): SecretKey {
        return SecretKeySpec(rawKey, "AES")
    }
}
