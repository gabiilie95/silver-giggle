package com.ilieinc.dontsleep.viewmodel.base

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ilieinc.core.data.dataStore
import com.ilieinc.core.data.getValue
import com.ilieinc.core.data.setValue
import com.ilieinc.core.util.StateHelper.startForegroundService
import com.ilieinc.core.util.StateHelper.stopService
import com.ilieinc.dontsleep.ui.model.CardUiEvent
import com.ilieinc.dontsleep.ui.model.CardUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class CardViewModel(
    application: Application,
    private val serviceClass: Class<*>,
    serviceRunning: StateFlow<Boolean>
) : AndroidViewModel(application) {

    abstract val tag: String
    abstract val timeoutPreferenceKey: Preferences.Key<Long>
    abstract val timeoutEnabledPreferenceKey: Preferences.Key<Boolean>
    abstract val showTimeoutSectionToggle: Boolean

    protected val context: Context get() = getApplication<Application>().applicationContext

    protected val _state = MutableStateFlow(CardUiState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            serviceRunning.collect { serviceRunning ->
                _state.update {
                    it.copy(
                        enabled = serviceRunning,
                        timeoutSectionToggleEnabled = !serviceRunning
                    )
                }
            }
        }
    }

    open fun refreshPermissionState() {}

    open fun <T> onEvent(event: T) where T : CardUiEvent = viewModelScope.launch {
        when (event) {
            is CardUiEvent.StatusToggleChanged -> setEnabled(event.enabled)
            is CardUiEvent.AutoOffToggleChanged -> timeoutChanged(event.enabled)
            is CardUiEvent.ChangeHelpDialogVisibility -> onChangeHelpDialogVisibility(event.visible)
            is CardUiEvent.ChangePermissionDialogVisibility -> onChangePermissionDialogVisibility(
                event.visible
            )

            is CardUiEvent.TimeoutTimeChanged -> updateTime(event.hours, event.minutes)
        }
    }

    protected suspend fun setSavedTime() {
        val timeout = context.dataStore.getValue(timeoutPreferenceKey, 900000) / 1000 / 60
        _state.update {
            it.copy(
                hours = (timeout / 60).toInt(),
                minutes = (timeout % 60).toInt()
            )
        }
    }

    protected fun updateTitle(title: String) {
        _state.update { it.copy(title = title) }
    }

    private suspend fun updateTime(hours: Int, minutes: Int) {
        _state.update {
            it.copy(
                hours = hours,
                minutes = minutes
            )
        }
        context.dataStore.setValue(
            timeoutPreferenceKey,
            ((hours * 60 + minutes) * 60 * 1000).toLong()
        )
    }

    protected suspend fun setSavedTimeoutStatus() {
        _state.update {
            it.copy(
                timeoutEnabled = context.dataStore.getValue(timeoutEnabledPreferenceKey, true)
            )
        }
    }

    private fun setEnabled(enabled: Boolean) {
        if (enabled) {
            startService()
        } else {
            stopService()
        }
        _state.update {
            it.copy(
                enabled = enabled,
                timeoutSectionToggleEnabled = !enabled
            )
        }
    }

    open fun startService() {
        context.startForegroundService(serviceClass)
    }

    open fun stopService() {
        context.stopService(serviceClass)
    }

    private fun onChangeHelpDialogVisibility(visible: Boolean) {
        _state.update { it.copy(showHelpDialog = visible) }
    }

    private fun onChangePermissionDialogVisibility(visible: Boolean) {
        _state.update { it.copy(showPermissionDialog = visible) }
    }

    private suspend fun timeoutChanged(enabled: Boolean) {
        withContext(Dispatchers.IO) {
            context.dataStore.setValue(timeoutEnabledPreferenceKey, enabled)
        }
        if (!enabled) {
            stopService()
        }
        _state.update { it.copy(timeoutEnabled = enabled) }
    }
}