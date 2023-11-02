package com.ilieinc.dontsleep.viewmodel.base

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ilieinc.core.util.SharedPreferenceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

abstract class CardViewModel(application: Application, serviceRunning: MutableStateFlow<Boolean>) :
    AndroidViewModel(application) {

    abstract val tag: String
    abstract val showTimeoutSectionToggle: Boolean
    abstract val timeoutEnabledTag: String

    var enabled = MutableStateFlow(false)

    var showPermissionDialog by mutableStateOf(false)
        protected set
    var title by mutableStateOf("")
        protected set
    var hours by mutableIntStateOf(0)
        protected set
    var minutes by mutableIntStateOf(0)
        protected set
    var timeoutEnabled by mutableStateOf(true)
        protected set
    var showHelpDialog by mutableStateOf(false)
        protected set
    var permissionRequired by mutableStateOf(false)
        protected set

    init {
        viewModelScope.launch {
            serviceRunning.collect { serviceRunning ->
                enabled.value = serviceRunning
            }
        }
        viewModelScope.launch {
            enabled.collect { enabled ->
                if (enabled) {
                    startService()
                } else {
                    stopService()
                }
            }
        }
    }

    abstract fun refreshPermissionState()

    protected fun setSavedTime() {
        val timeout =
            SharedPreferenceManager.getInstance(getApplication()).getLong(tag, 900000) / 1000 / 60
        hours = (timeout / 60).toInt()
        minutes = (timeout % 60).toInt()
    }

    protected fun setSavedTimeoutStatus() {
        timeoutEnabled = SharedPreferenceManager.getInstance(getApplication())
            .getBoolean(timeoutEnabledTag, true)
    }

    fun updateTime(hours: Int, minutes: Int) =
        with(SharedPreferenceManager.getInstance(getApplication()).edit()) {
            putLong(tag, ((hours * 60 + minutes) * 60 * 1000).toLong())
            apply()
        }

    abstract fun startService()

    abstract fun stopService()

    fun onShowHelpDialog() {
        showHelpDialog = true
    }

    fun onShowPermissionDialog() {
        showPermissionDialog = true
    }

    fun onDismissHelpDialog() {
        showHelpDialog = false
    }

    fun onDismissPermissionDialog() {
        showPermissionDialog = false
    }

    fun timeoutChanged(enabled: Boolean) {
        timeoutEnabled = enabled
        with(SharedPreferenceManager.getInstance(getApplication()).edit()) {
            putBoolean(timeoutEnabledTag, timeoutEnabled)
            apply()
        }
        if (!timeoutEnabled) {
            stopService()
        }
    }
}