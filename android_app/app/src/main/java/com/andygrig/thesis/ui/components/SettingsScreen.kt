package com.andygrig.thesis.ui.components

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.andygrig.thesis.ui.capture.PortraitCaptureActivity
import com.andygrig.thesis.viewmodel.MainViewModel
import org.apache.commons.codec.binary.Base32

@Composable
fun SettingsScreen(vm: MainViewModel) {
    val activity = LocalContext.current as Activity
    var manual by remember { mutableStateOf(TextFieldValue("")) }

    val qrLauncher = rememberLauncherForActivityResult(
        StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.getStringExtra("SCAN_RESULT")
            if (uri.isNullOrBlank()) {
                Toast.makeText(activity, "Не получили результат сканирования", Toast.LENGTH_SHORT).show()
                return@rememberLauncherForActivityResult
            }
            processUri(uri, activity, vm)
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text("TOTP", style = MaterialTheme.typography.headlineMedium)

            OutlinedTextField(
                value = manual,
                onValueChange = { manual = it },
                label = { Text("otpauth://…") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(0.9f)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(56.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        qrLauncher.launch(
                            Intent(activity, PortraitCaptureActivity::class.java)
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    Icon(Icons.Filled.QrCodeScanner, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("QR")
                }

                Button(
                    onClick = {
                        val uri = manual.text.trim()
                        processUri(uri, activity, vm)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    Text("Сохранить")
                }
            }
        }
    }
}

private fun processUri(uri: String, activity: Activity, vm: MainViewModel) {
    if (!uri.startsWith("otpauth://totp/")) {
        Toast.makeText(activity, "Неверный формат URI", Toast.LENGTH_SHORT).show()
        return
    }
    val id = uri.substringAfter("totp/").substringBefore('?')
    val secretB32 = uri.substringAfter("secret=").substringBefore('&')
    try {
        val raw = Base32().decode(secretB32)
        vm.saveSecret(id, raw)
    } catch (e: Exception) {
        Toast.makeText(activity, "Ошибка разбора секрета", Toast.LENGTH_SHORT).show()
    }
}
