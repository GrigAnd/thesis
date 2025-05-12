package com.andygrig.thesis.data

import android.content.Context
import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

object CipherHelper {
    private const val PREFS = "totp_prefs"
    private const val KEY_IV = "cipher_iv"
    private const val KEY_DATA = "cipher_data"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val IV_SIZE = 12
    private const val TAG_SIZE = 128

    fun encryptAndStore(ctx: Context, key: SecretKey, data: ByteArray) {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val iv = cipher.iv
        val encrypted = cipher.doFinal(data)

        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_IV, Base64.encodeToString(iv, Base64.DEFAULT))
            .putString(KEY_DATA, Base64.encodeToString(encrypted, Base64.DEFAULT))
            .apply()
    }

    fun loadAndDecrypt(ctx: Context, key: SecretKey): ByteArray? {
        val prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val ivB64 = prefs.getString(KEY_IV, null) ?: return null
        val dataB64 = prefs.getString(KEY_DATA, null) ?: return null

        val iv = Base64.decode(ivB64, Base64.DEFAULT)
        val encrypted = Base64.decode(dataB64, Base64.DEFAULT)

        val cipher = Cipher.getInstance(TRANSFORMATION)
        val spec = GCMParameterSpec(TAG_SIZE, iv)
        cipher.init(Cipher.DECRYPT_MODE, key, spec)
        return cipher.doFinal(encrypted)
    }
}
