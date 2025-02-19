package com.example.migration.encryption

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import com.example.migration.encryption.lib.AesGcmJce
import com.example.migration.encryption.lib.AesSiv
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

class EncryptedDataStore(context: Context) {
    private val keyAlias = "my_master_key"
    private val prefs = context.getSharedPreferences("encrypted_prefs", Context.MODE_PRIVATE)
    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
    private val masterKey: SecretKey = getOrCreateMasterKey()

    private val keyset: SecretKey = getOrCreateKeyset()

    fun encryptKey(key: String): String {
        val encrypted = AesSiv(keyset.encoded).encrypt(key.toByteArray(), byteArrayOf())
        return Base64.encodeToString(encrypted, Base64.DEFAULT)
    }

    fun decryptKey(encryptedKey: String): String {
        val decoded = Base64.decode(encryptedKey, Base64.DEFAULT)
        return String(AesSiv(keyset.encoded).decrypt(decoded, byteArrayOf()))
    }

    fun encryptValue(value: String): String {
        val encrypted = AesGcmJce(keyset.encoded).encrypt(value.toByteArray(), byteArrayOf())
        return Base64.encodeToString(encrypted, Base64.DEFAULT)
    }

    fun decryptValue(encryptedValue: String): String {
        val decoded = Base64.decode(encryptedValue, Base64.DEFAULT)
        return String(AesGcmJce(keyset.encoded).decrypt(decoded, byteArrayOf()))
    }

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

    private fun getOrCreateKeyset(): SecretKey {
        val encryptedKeyset = prefs.getString("encrypted_keyset", null)
        return if (encryptedKeyset != null) {
            decryptKeyset(encryptedKeyset)
        } else {
            val newKeyset = generateKeyset()
            prefs.edit().putString("encrypted_keyset", encryptKeyset(newKeyset)).apply()
            newKeyset
        }
    }

    private fun generateKeyset(): SecretKey {
        val keyGen = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES)
        keyGen.init(256)
        return keyGen.generateKey()
    }

    private fun encryptKeyset(keyset: SecretKey): String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, masterKey)
        val iv = cipher.iv
        val encrypted = cipher.doFinal(keyset.encoded)
        return Base64.encodeToString(iv + encrypted, Base64.DEFAULT)
    }

    private fun decryptKeyset(encryptedKeyset: String): SecretKey {
        val data = Base64.decode(encryptedKeyset, Base64.DEFAULT)
        val iv = data.copyOfRange(0, 12)
        val encrypted = data.copyOfRange(12, data.size)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, masterKey, GCMParameterSpec(128, iv))
        val keyBytes = cipher.doFinal(encrypted)

        return SecretKeySpec(keyBytes, "AES")
    }
}
