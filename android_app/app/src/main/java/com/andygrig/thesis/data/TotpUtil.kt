package com.andygrig.thesis.data

import java.lang.System.currentTimeMillis
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.math.pow
import kotlin.experimental.and

object TotpUtil {
    private const val TIME_STEP = 30L
    private const val DIGITS = 6

    fun generate(secret: ByteArray, time: Long = currentTimeMillis() / 1000): String {
        val counter = time / TIME_STEP
        val msg = ByteArray(8) { i ->
            ((counter shr (8 * (7 - i))) and 0xff).toByte()
        }
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(secret, "HmacSHA256"))
        val hash = mac.doFinal(msg)
        val offset = (hash.last() and 0x0f).toInt()
        val binary = ((hash[offset].toInt() and 0x7f) shl 24) or
                ((hash[offset+1].toInt() and 0xff) shl 16) or
                ((hash[offset+2].toInt() and 0xff) shl 8) or
                (hash[offset+3].toInt() and 0xff)
        val otp = binary % (10.0.pow(DIGITS).toInt())
        return otp.toString().padStart(DIGITS, '0')
    }
}
