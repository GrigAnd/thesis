package com.andygrig.thesis.data

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class KeystoreHelper(private val ctx: Context) {
    private val ALIAS = "TOTP_SECRET"
    private val ks: KeyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }

    private fun ensureKey() {
        if (ks.containsAlias(ALIAS)) return
        val kg = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        val spec = KeyGenParameterSpec.Builder(
            ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()
        kg.init(spec)
        kg.generateKey()
    }

    fun store(secret: ByteArray) {
        ensureKey()
        val key = ks.getKey(ALIAS, null) as SecretKey
        CipherHelper.encryptAndStore(ctx, key, secret)
    }

    fun load(): ByteArray? {
        if (!ks.containsAlias(ALIAS)) return null
        val key = ks.getKey(ALIAS, null) as SecretKey
        return CipherHelper.loadAndDecrypt(ctx, key)
    }

    fun delete() {
        if (ks.containsAlias(ALIAS)) {
            ks.deleteEntry(ALIAS)
        }
        ctx.getSharedPreferences("totp_prefs", Context.MODE_PRIVATE)
            .edit().clear().apply()
    }
}
