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

    companion object {
        private const val KEY_ALIAS = "my_master_key"
        private const val PREFS_NAME = "encrypted_prefs"
        private const val KEYSET_PREFS_KEY = "encrypted_keyset"
        private const val KEYSTORE_TYPE = "AndroidKeyStore"
        private const val AES_ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        private const val AES_MODE_GCM = "AES/GCM/NoPadding"
        private const val AES_KEY_SIZE = 256
        private const val GCM_TAG_SIZE = 128
        private const val IV_SIZE = 12
    }

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val keyStore = KeyStore.getInstance(KEYSTORE_TYPE).apply { load(null) }
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
        return if (keyStore.containsAlias(KEY_ALIAS)) {
            val entry = keyStore.getEntry(KEY_ALIAS, null) as KeyStore.SecretKeyEntry
            entry.secretKey
        } else {
            val keyGen = KeyGenerator.getInstance(AES_ALGORITHM, KEYSTORE_TYPE)
            keyGen.init(
                KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setKeySize(AES_KEY_SIZE)
                    .build()
            )
            keyGen.generateKey()
        }
    }

    private fun getOrCreateKeyset(): SecretKey {
        val encryptedKeyset = prefs.getString(KEYSET_PREFS_KEY, null)
        return if (encryptedKeyset != null) {
            decryptKeyset(encryptedKeyset)
        } else {
            val newKeyset = generateKeyset()
            prefs.edit().putString(KEYSET_PREFS_KEY, encryptKeyset(newKeyset)).apply()
            newKeyset
        }
    }

    private fun generateKeyset(): SecretKey {
        val keyGen = KeyGenerator.getInstance(AES_ALGORITHM)
        keyGen.init(AES_KEY_SIZE)
        return keyGen.generateKey()
    }

    private fun encryptKeyset(keyset: SecretKey): String {
        val cipher = Cipher.getInstance(AES_MODE_GCM)
        cipher.init(Cipher.ENCRYPT_MODE, masterKey)
        val iv = cipher.iv
        val encrypted = cipher.doFinal(keyset.encoded)
        return Base64.encodeToString(iv + encrypted, Base64.DEFAULT)
    }

    private fun decryptKeyset(encryptedKeyset: String): SecretKey {
        val data = Base64.decode(encryptedKeyset, Base64.DEFAULT)
        val iv = data.copyOfRange(0, IV_SIZE)
        val encrypted = data.copyOfRange(IV_SIZE, data.size)

        val cipher = Cipher.getInstance(AES_MODE_GCM)
        cipher.init(Cipher.DECRYPT_MODE, masterKey, GCMParameterSpec(GCM_TAG_SIZE, iv))
        val keyBytes = cipher.doFinal(encrypted)

        return SecretKeySpec(keyBytes, AES_ALGORITHM)
    }
}