package com.andygrig.thesis.ui.components

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix

@Composable
fun QrCodeView(
    data: String,
    modifier: Modifier = Modifier,
    size: Dp = 200.dp
) {
    val img = remember(data) {
        generateQrBitmap(data, size.value.toInt())
    }
    Image(bitmap = img, contentDescription = "QR Code", modifier = modifier)
}

private fun generateQrBitmap(text: String, pixels: Int): androidx.compose.ui.graphics.ImageBitmap {
    val hints = mapOf(EncodeHintType.MARGIN to 1)
    val bitMatrix: BitMatrix = MultiFormatWriter()
        .encode(text, BarcodeFormat.QR_CODE, pixels, pixels, hints)

    val bmp = Bitmap.createBitmap(pixels, pixels, Bitmap.Config.ARGB_8888)
    for (x in 0 until pixels) {
        for (y in 0 until pixels) {
            bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
        }
    }
    return bmp.asImageBitmap()
}
