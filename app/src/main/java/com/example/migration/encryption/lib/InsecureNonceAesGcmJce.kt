package com.example.migration.encryption.lib

import android.security.keystore.KeyProperties.BLOCK_MODE_GCM
import android.security.keystore.KeyProperties.ENCRYPTION_PADDING_NONE
import android.security.keystore.KeyProperties.KEY_ALGORITHM_AES
import java.security.spec.AlgorithmParameterSpec
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

class InsecureNonceAesGcmJce(key: ByteArray) {
    companion object {
        const val IV_SIZE_IN_BYTES: Int = 12
        const val TAG_SIZE_IN_BYTES: Int = 16

        private const val TRANSFORMATION = "$KEY_ALGORITHM_AES/$BLOCK_MODE_GCM/$ENCRYPTION_PADDING_NONE"
    }

    private val cipher: Cipher = Cipher.getInstance(TRANSFORMATION)

    private val keySpec: SecretKey = SecretKeySpec(key, "AES")

    fun encrypt(iv: ByteArray, plaintext: ByteArray): ByteArray {
        val ciphertextLength = IV_SIZE_IN_BYTES + plaintext.size + TAG_SIZE_IN_BYTES
        val ciphertext = ByteArray(ciphertextLength)
        System.arraycopy(iv, 0, ciphertext, 0, IV_SIZE_IN_BYTES)

        val params = getParams(iv)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, params)
        val ciphertextOutputOffset = IV_SIZE_IN_BYTES
        cipher.doFinal(
            plaintext,
            0,
            plaintext.size,
            ciphertext,
            ciphertextOutputOffset
        )
        return ciphertext
    }

    fun decrypt(iv: ByteArray, ciphertext: ByteArray): ByteArray {
        val params = getParams(iv)
        cipher.init(Cipher.DECRYPT_MODE, keySpec, params)
        val ciphertextInputOffset = IV_SIZE_IN_BYTES
        val ciphertextLength = ciphertext.size - IV_SIZE_IN_BYTES
        return cipher.doFinal(ciphertext, ciphertextInputOffset, ciphertextLength)
    }

    private fun getParams(iv: ByteArray): AlgorithmParameterSpec {
        return GCMParameterSpec(8 * TAG_SIZE_IN_BYTES, iv, 0, iv.size)
    }
}
