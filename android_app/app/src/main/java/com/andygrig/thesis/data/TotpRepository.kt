package com.andygrig.thesis.data

import android.content.Context

class TotpRepository(context: Context) {
    private val keystore = KeystoreHelper(context)
    private val prefs = context.getSharedPreferences("totp_prefs", Context.MODE_PRIVATE)

    fun hasSecret(): Boolean =
        prefs.contains("id") && keystore.load() != null

    fun save(id: String, rawSecret: ByteArray) {
        prefs.edit().putString("id", id).apply()
        keystore.store(rawSecret)
    }

    fun delete() {
        prefs.edit().clear().apply()
        keystore.delete()
    }

    fun loadId(): String =
        prefs.getString("id", "")!!

    fun generateCodeAtInterval(interval: Long): String {
        val secret = keystore.load()
            ?: throw IllegalStateException("No secret")
        return TotpUtil.generate(secret, interval * 30)
    }
}
