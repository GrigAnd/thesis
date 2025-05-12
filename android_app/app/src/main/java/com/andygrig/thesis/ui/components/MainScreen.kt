package com.andygrig.thesis.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.andygrig.thesis.viewmodel.MainViewModel

@Composable
fun MainScreen(vm: MainViewModel) {
    val code by vm.code.collectAsState()
    val id by vm.id.collectAsState()
    val remaining by vm.remaining.collectAsState()

    val screenDp = LocalConfiguration.current.screenWidthDp.dp
    val qrSize = screenDp * 0.9f

    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            QrCodeView(
                data = "$id:$code",
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .aspectRatio(1f)
            )
            Text(text = "$id:$code", style = MaterialTheme.typography.bodyLarge)
            Text("Обновление через $remaining с", style = MaterialTheme.typography.bodyLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { vm.refresh() }) {
                    Text("Следующий код")
                }
                Button(
                    onClick = { vm.deleteSecret() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    Text("Удалить")
                }
            }
        }
    }
}
