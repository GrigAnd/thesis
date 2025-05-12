package com.andygrig.thesis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.andygrig.thesis.ui.components.MainScreen
import com.andygrig.thesis.ui.components.SettingsScreen
import com.andygrig.thesis.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    private val vm by lazy { MainViewModel(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val has by vm.hasSecret.collectAsState()
            if (has) MainScreen(vm) else SettingsScreen(vm)
        }
    }
}
