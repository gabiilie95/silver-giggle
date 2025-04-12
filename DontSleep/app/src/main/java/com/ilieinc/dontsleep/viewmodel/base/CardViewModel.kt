package com.ilieinc.dontsleep.viewmodel.base

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.ilieinc.core.data.dataStore
import com.ilieinc.core.data.getValue
import com.ilieinc.core.data.setValue
import com.ilieinc.core.util.Logger
import com.ilieinc.core.util.StateHelper.startForegroundService
import com.ilieinc.core.util.StateHelper.stopService
import com.ilieinc.dontsleep.ui.model.CardUiEvent
import com.ilieinc.dontsleep.ui.model.CardUiEvent.OnAddButtonClick
import com.ilieinc.dontsleep.ui.model.CardUiEvent.OnAutoOffToggleChange
import com.ilieinc.dontsleep.ui.model.CardUiEvent.OnCancelButtonClick
import com.ilieinc.dontsleep.ui.model.CardUiEvent.OnChangeHelpDialogVisibility
import com.ilieinc.dontsleep.ui.model.CardUiEvent.OnChangePermissionDialogVisibility
import com.ilieinc.dontsleep.ui.model.CardUiEvent.OnDeleteButtonClick
import com.ilieinc.dontsleep.ui.model.CardUiEvent.OnExpandedDropdownChanged
import com.ilieinc.dontsleep.ui.model.CardUiEvent.OnSaveButtonClick
import com.ilieinc.dontsleep.ui.model.CardUiEvent.OnSavedTimeSelectionChange
import com.ilieinc.dontsleep.ui.model.CardUiEvent.OnStatusToggleChange
import com.ilieinc.dontsleep.ui.model.CardUiEvent.OnTimeoutModeButtonClick
import com.ilieinc.dontsleep.ui.model.CardUiEvent.OnTimeoutTimeChange
import com.ilieinc.dontsleep.ui.model.CardUiState
import com.ilieinc.dontsleep.ui.model.common.SelectedTime
import kotlinx.coroutines.CoroutineExceptionHandler
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
    abstract val statePreferenceKey: Preferences.Key<String>
    abstract val showTimeoutSectionToggle: Boolean

    protected val context: Context get() = getApplication<Application>().applicationContext

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        Logger.error(exception)
    }

    private val ioScope = Dispatchers.IO + exceptionHandler

    protected val _state = MutableStateFlow(CardUiState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch(ioScope) {
            serviceRunning.collect { serviceRunning ->
                _state.update {
                    it.copy(enabled = serviceRunning)
                }
            }
        }
    }

    open fun refreshPermissionState() {}

    open fun <T> onEvent(event: T) where T : CardUiEvent = viewModelScope.launch(ioScope) {
        when (event) {
            is OnStatusToggleChange -> setEnabled(event.enabled)
            is OnAutoOffToggleChange -> timeoutChanged(event.enabled)
            is OnChangeHelpDialogVisibility -> onChangeHelpDialogVisibility(event.visible)
            is OnChangePermissionDialogVisibility -> onChangePermissionDialogVisibility(event.visible)
            is OnTimeoutTimeChange -> updateTimeout(event.hours, event.minutes)
            is OnTimeoutModeButtonClick -> onTimeoutModeButtonClick(event.state)
            OnAddButtonClick -> onAddButtonClick()
            OnCancelButtonClick -> onCancelButtonClick()
            is OnSaveButtonClick -> onSaveButtonClick(event.hour, event.minute, event.isAfternoon)
            OnDeleteButtonClick -> onDeleteButtonClick()
            is OnExpandedDropdownChanged -> onExpandedDropdownChanged(event.expanded)
            is OnSavedTimeSelectionChange -> onSelectionChange(event.savedTime)
        }
    }

    protected suspend fun setSavedState() {
        context.dataStore.getValue(statePreferenceKey, "").let { stateJson ->
            if (stateJson.isNotEmpty()) {
                runCatching {
                    val timeoutState = Gson().fromJson(stateJson, CardUiState::class.java)
                    withContext(Dispatchers.Main) {
                        _state.update { timeoutState }
                    }
                }.onFailure {
                    Logger.error("Failed to parse saved state", it)
                    _state.update {
                        CardUiState()
                    }
                }
            }
        }
    }

    protected fun updateTitle(title: String) {
        _state.update { it.copy(title = title) }
    }

    private fun updateTimeout(hours: Int, minutes: Int) {
        _state.update {
            it.copy(
                timeoutState = it.timeoutState.copy(
                    selectedTime = SelectedTime(hours, minutes)
                )
            ).also(::updateState)
        }
    }

    private fun timeoutChanged(enabled: Boolean) {
        if (!enabled) {
            stopService()
        }
        _state.update {
            it.copy(
                timeoutEnabled = enabled
            ).also(::updateState)
        }
    }

    private fun onTimeoutModeButtonClick(state: CardUiState) {
        _state.update {
            it.copy(
                timeoutMode = if (state.timeoutMode == CardUiState.TimeoutMode.TIMEOUT) {
                    CardUiState.TimeoutMode.CLOCK
                } else {
                    CardUiState.TimeoutMode.TIMEOUT
                }
            ).also(::updateState)
        }
    }

    private fun onAddButtonClick() {
        _state.update {
            it.copy(
                clockState = it.clockState.copy(
                    isAddingNewTime = true
                )
            )
        }
    }

    private fun onCancelButtonClick() {
        _state.update {
            it.copy(
                clockState = it.clockState.copy(
                    isAddingNewTime = false
                )
            )
        }
    }

    private fun onSaveButtonClick(hour: Int, minute: Int, isAfternoon: Boolean) {
        val newSavedTime = SelectedTime(hour, minute)
        _state.update {
            it.copy(
                clockState = with(it.clockState) {
                    copy(
                        selectedTime = newSavedTime,
                        isAddingNewTime = false,
                        savedTimes = savedTimes.toMutableList().apply {
                            if (!contains(newSavedTime)) {
                                add(newSavedTime)
                            }
                        }
                    )
                }
            ).also(::updateState)
        }
    }

    private fun onDeleteButtonClick() {
        _state.update {
            val updatedItems = it.clockState.savedTimes.toMutableList().apply {
                remove(state.value.clockState.selectedTime)
            }
            it.copy(
                clockState = it.clockState.copy(
                    selectedTime = updatedItems.firstOrNull(),
                    isAddingNewTime = false,
                    savedTimes = updatedItems
                )
            ).also(::updateState)
        }
    }

    private fun onExpandedDropdownChanged(expanded: Boolean) {
        if (_state.value.clockState.savedTimes.isEmpty()) return
        _state.update {
            it.copy(
                clockState = it.clockState.copy(
                    isDropdownExpanded = expanded
                )
            )
        }
    }

    private fun onSelectionChange(savedTime: SelectedTime) {
        _state.update {
            it.copy(
                clockState = it.clockState.copy(
                    selectedTime = savedTime,
                    isDropdownExpanded = false
                )
            ).also(::updateState)
        }
    }

    private fun setEnabled(enabled: Boolean) {
        if (enabled) {
            startService()
        } else {
            stopService()
        }
        _state.update {
            it.copy(enabled = enabled)
        }
    }

    private fun updateState(state: CardUiState) {
        viewModelScope.launch(ioScope) {
            context.dataStore.setValue(
                statePreferenceKey,
                Gson().toJson(state)
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
}
