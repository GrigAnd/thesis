package com.andygrig.thesis.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andygrig.thesis.data.TotpRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MainViewModel(context: Context) : ViewModel() {
    private val repo = TotpRepository(context)

    private val _has = MutableStateFlow(repo.hasSecret())
    val hasSecret: StateFlow<Boolean> = _has.asStateFlow()

    private val _id = MutableStateFlow(repo.loadId().takeIf { repo.hasSecret() } ?: "")
    val id: StateFlow<String> = _id.asStateFlow()

    private val _code = MutableStateFlow("------")
    val code: StateFlow<String> = _code.asStateFlow()

    private val _remaining = MutableStateFlow(0)
    val remaining: StateFlow<Int> = _remaining.asStateFlow()

    private var tickerJob: Job? = null
    private var currentInterval: Long = 0
    private var nextTransition: Long = 0

    init {
        if (repo.hasSecret()) {
            startTicker()
        }
    }

    private fun startTicker(startAt: Long? = null) {
        tickerJob?.cancel()

        tickerJob = viewModelScope.launch {
            val nowSec = System.currentTimeMillis() / 1000
            currentInterval = startAt ?: (nowSec / 30)
            _code.value = repo.generateCodeAtInterval(currentInterval)
            nextTransition = (currentInterval + 1) * 30

            while (isActive && repo.hasSecret()) {
                val now = System.currentTimeMillis() / 1000
                _remaining.value = (nextTransition - now).toInt().coerceAtLeast(0)

                if (now >= nextTransition) {
                    currentInterval = now / 30
                    _code.value = repo.generateCodeAtInterval(currentInterval)
                    nextTransition = (currentInterval + 1) * 30
                }
                delay(1_000)
            }

            _remaining.value = 0
            _code.value = "------"
        }
    }

    fun refresh() {
        if (!repo.hasSecret()) return
        startTicker(currentInterval + 1)
    }

    fun saveSecret(id: String, raw: ByteArray) {
        repo.save(id, raw)
        _has.value = true
        _id.value = id
        startTicker()
    }

    fun deleteSecret() {
        tickerJob?.cancel()
        repo.delete()
        _has.value = false
        _id.value = ""
        _remaining.value = 0
        _code.value = "------"
    }
}
