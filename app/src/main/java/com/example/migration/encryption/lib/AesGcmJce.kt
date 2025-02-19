package com.example.migration.encryption.lib

import java.security.GeneralSecurityException
import java.security.SecureRandom

class AesGcmJce(key: ByteArray) {
    private val insecureNonceAesGcmJce = InsecureNonceAesGcmJce(key)

    @Throws(GeneralSecurityException::class)
    fun encrypt(plaintext: ByteArray, associatedData: ByteArray): ByteArray {
        val secureRandom = SecureRandom()
        val rand = ByteArray(InsecureNonceAesGcmJce.IV_SIZE_IN_BYTES)
        secureRandom.nextBytes(rand)
        return insecureNonceAesGcmJce.encrypt(rand, plaintext)
    }

    @Throws(GeneralSecurityException::class)
    fun decrypt(ciphertext: ByteArray, associatedData: ByteArray): ByteArray {
        val iv = ciphertext.copyOf(InsecureNonceAesGcmJce.IV_SIZE_IN_BYTES)
        return insecureNonceAesGcmJce.decrypt(iv, ciphertext)
    }
}