package com.example.migration.encryption.lib

import android.security.keystore.KeyProperties.BLOCK_MODE_CTR
import android.security.keystore.KeyProperties.ENCRYPTION_PADDING_NONE
import android.security.keystore.KeyProperties.KEY_ALGORITHM_AES
import com.google.crypto.tink.mac.internal.AesUtil
import com.google.crypto.tink.subtle.Bytes
import com.google.crypto.tink.subtle.EngineFactory
import com.google.crypto.tink.subtle.PrfAesCmac
import java.security.GeneralSecurityException
import java.util.Arrays
import javax.crypto.AEADBadTagException
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class AesSiv(key: ByteArray) {

    companion object {
        private val KEY_SIZES: Collection<Int> = mutableListOf(64)
        private val BLOCK_ZERO: ByteArray = ByteArray(AesUtil.BLOCK_SIZE)
        private val BLOCK_ONE: ByteArray = byteArrayOf(
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0x01.toByte()
        )

        private const val TRANSFORMATION =
            "$KEY_ALGORITHM_AES/$BLOCK_MODE_CTR/$ENCRYPTION_PADDING_NONE"
    }

    private val cipher: Cipher = Cipher.getInstance(TRANSFORMATION)

    private val aesCtrKey: ByteArray
    private val cmacForS2V: PrfAesCmac

    init {
        val k1: ByteArray = Arrays.copyOfRange(key, 0, key.size / 2)
        aesCtrKey = Arrays.copyOfRange(key, key.size / 2, key.size)
        cmacForS2V = PrfAesCmac(k1)
    }

    fun encrypt(plaintext: ByteArray, associatedData: ByteArray): ByteArray {
        val computedIv = s2v(associatedData, plaintext)
        val ivForJavaCrypto = computedIv.clone()
        ivForJavaCrypto[8] = (ivForJavaCrypto[8].toInt() and 0x7F).toByte()
        ivForJavaCrypto[12] = (ivForJavaCrypto[12].toInt() and 0x7F).toByte()

        cipher.init(
            Cipher.ENCRYPT_MODE,
            SecretKeySpec(aesCtrKey, "AES"),
            IvParameterSpec(ivForJavaCrypto)
        )

        val ctrCiphertext: ByteArray = cipher.doFinal(plaintext)
        return Bytes.concat(computedIv, ctrCiphertext)
    }

    fun decrypt(ciphertext: ByteArray, associatedData: ByteArray): ByteArray {
        val aesCtr = EngineFactory.CIPHER.getInstance("AES/CTR/NoPadding")

        val expectedIv = Arrays.copyOfRange(ciphertext, 0, AesUtil.BLOCK_SIZE)

        val ivForJavaCrypto = expectedIv.clone()
        ivForJavaCrypto[8] = (ivForJavaCrypto[8].toInt() and 0x7F).toByte()
        ivForJavaCrypto[12] = (ivForJavaCrypto[12].toInt() and 0x7F).toByte()

        aesCtr.init(
            Cipher.DECRYPT_MODE,
            SecretKeySpec(this.aesCtrKey, "AES"),
            IvParameterSpec(ivForJavaCrypto)
        )

        val ctrCiphertext = Arrays.copyOfRange(ciphertext, AesUtil.BLOCK_SIZE, ciphertext.size)
        val decryptedPt = aesCtr.doFinal(ctrCiphertext)
        val computedIv = s2v(associatedData, decryptedPt)

        if (Bytes.equal(expectedIv, computedIv)) {
            return decryptedPt!!
        } else {
            throw AEADBadTagException("Integrity check failed.")
        }
    }

    @Throws(GeneralSecurityException::class)
    private fun s2v(vararg s: ByteArray?): ByteArray {
        if (s.isEmpty()) {
            return cmacForS2V.compute(BLOCK_ONE, AesUtil.BLOCK_SIZE)
        }

        var result = cmacForS2V.compute(BLOCK_ZERO, AesUtil.BLOCK_SIZE)
        for (i in 0 until s.size - 1) {
            val currBlock = s[i] ?: ByteArray(0)
            result =
                Bytes.xor(AesUtil.dbl(result), cmacForS2V.compute(currBlock, AesUtil.BLOCK_SIZE))
        }
        val lastBlock = s[s.size - 1]
        result = if (lastBlock!!.size >= 16) {
            Bytes.xorEnd(lastBlock, result)
        } else {
            Bytes.xor(AesUtil.cmacPad(lastBlock), AesUtil.dbl(result))
        }
        return cmacForS2V.compute(result, AesUtil.BLOCK_SIZE)
    }
}
