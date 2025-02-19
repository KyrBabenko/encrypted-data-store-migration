package com.example.migration.encryption

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.example.migration.encryption.lib.AesGcmJce
import com.example.migration.encryption.lib.AesSiv
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class EncryptedDataStore {
    private val keyAlias = "my_master_key"
    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
    private val masterKey: SecretKey = getOrCreateMasterKey()

    private val keyEncryption = AesSiv(masterKey.encoded)
    private val valueEncryption = AesGcmJce(masterKey.encoded)

    private fun getOrCreateMasterKey(): SecretKey {
        return if (keyStore.containsAlias(keyAlias)) {
            val entry = keyStore.getEntry(keyAlias, null) as KeyStore.SecretKeyEntry
            entry.secretKey
        } else {
            val keyGen = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
            keyGen.init(
                KeyGenParameterSpec.Builder(
                    keyAlias,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setKeySize(256)
                    .build()
            )
            keyGen.generateKey()
        }
    }

    fun encryptKey(key: String): ByteArray {
        return keyEncryption.encrypt(key.toByteArray(), byteArrayOf())
    }

    fun decryptKey(encryptedKey: ByteArray): String {
        return String(keyEncryption.decrypt(encryptedKey, byteArrayOf()))
    }

    fun encryptValue(value: String): ByteArray {
        return valueEncryption.encrypt(value.toByteArray(), byteArrayOf())
    }

    fun decryptValue(encryptedValue: ByteArray): String {
        return String(valueEncryption.decrypt(encryptedValue, byteArrayOf()))
    }
}
