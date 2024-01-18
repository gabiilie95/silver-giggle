package com.ilieinc.dontsleep.viewmodel.base

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ilieinc.core.util.SharedPreferenceManager
import com.ilieinc.dontsleep.ui.model.CardUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class CardViewModel(application: Application, serviceRunning: MutableStateFlow<Boolean>) : AndroidViewModel(application) {

    abstract val tag: String
    abstract val showTimeoutSectionToggle: Boolean
    abstract val timeoutEnabledTag: String

    protected val context: Context get() = getApplication<Application>().applicationContext

    var uiModel = MutableStateFlow(CardUiModel())

    init {
        viewModelScope.launch {
            serviceRunning.collect { serviceRunning ->
                uiModel.update {
                    it.copy(
                        enabled = serviceRunning,
                        timeoutSectionToggleEnabled = !serviceRunning
                    )
                }
            }
        }
    }

    abstract fun refreshPermissionState()

    protected fun setSavedTime() {
        val timeout =
            SharedPreferenceManager.getInstance(getApplication()).getLong(tag, 900000) / 1000 / 60
        updateTime(timeout)
    }

    protected fun updateTitle(title: String) {
        uiModel.update { it.copy(title = title) }
    }

    private fun updateTime(timeout: Long){
        uiModel.update {
            it.copy(
                hours = (timeout / 60).toInt(),
                minutes = (timeout % 60).toInt()
            )
        }
    }

    protected fun setSavedTimeoutStatus() {
        uiModel.update {
            it.copy(
                timeoutEnabled = SharedPreferenceManager.getInstance(getApplication())
                    .getBoolean(timeoutEnabledTag, true)
            )
        }
    }

    fun setEnabled(enabled: Boolean) {
        if (enabled) {
            startService()
        } else {
            stopService()
        }
        uiModel.update {
            it.copy(
                enabled = enabled,
                timeoutSectionToggleEnabled = !enabled
            )
        }
    }

    fun updateTime(hours: Int, minutes: Int) =
        with(SharedPreferenceManager.getInstance(getApplication()).edit()) {
            putLong(tag, ((hours * 60 + minutes) * 60 * 1000).toLong())
            apply()
        }

    abstract fun startService()

    abstract fun stopService()

    fun onShowHelpDialog() {
        uiModel.update { it.copy(showHelpDialog = true) }
    }

    fun onShowPermissionDialog() {
        uiModel.update { it.copy(showPermissionDialog = true) }
    }

    fun onDismissHelpDialog() {
        uiModel.update { it.copy(showHelpDialog = false) }
    }

    fun onDismissPermissionDialog() {
        uiModel.update { it.copy(showPermissionDialog = false) }
    }

    fun timeoutChanged(enabled: Boolean) {
        with(SharedPreferenceManager.getInstance(getApplication()).edit()) {
            putBoolean(timeoutEnabledTag, enabled)
            apply()
        }
        if (!enabled) {
            stopService()
        }
        uiModel.update { it.copy(timeoutEnabled = enabled) }
    }
}