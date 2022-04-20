package com.ilieinc.dontsleep.viewmodel.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ilieinc.dontsleep.util.SharedPreferenceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

abstract class CardViewModel(application: Application, serviceRunning: MutableStateFlow<Boolean>) :
    AndroidViewModel(application) {
    val showPermissionDialog = MutableStateFlow(false)
    abstract val tag: String

    val title = MutableStateFlow("")
    val hours = MutableStateFlow(0)
    val minutes = MutableStateFlow(0)
    val enabled = MutableStateFlow(false)
    val showHelpDialog = MutableStateFlow(false)
    val permissionRequired = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            serviceRunning.collect { serviceRunning ->
                enabled.tryEmit(serviceRunning)
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

    protected fun setSavedTime(){
        val timeout = SharedPreferenceManager.getInstance(getApplication()).getLong(tag, 900000) / 1000 / 60
        hours.tryEmit((timeout / 60).toInt())
        minutes.tryEmit((timeout % 60).toInt())
    }

    fun updateTime(hours: Int, minutes: Int) {
        val editor = SharedPreferenceManager.getInstance(getApplication()).edit()
        editor.putLong(tag, ((hours * 60 + minutes) * 60 * 1000).toLong())
        editor.apply()
    }

    abstract fun startService()

    abstract fun stopService()
}