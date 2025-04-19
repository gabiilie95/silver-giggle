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
import com.ilieinc.dontsleep.ui.model.CardUiEvent.*
import com.ilieinc.dontsleep.ui.model.CardUiState
import com.ilieinc.dontsleep.ui.model.common.ClockState.EditMode
import com.ilieinc.dontsleep.ui.model.common.ClockState.TimepickerMode
import com.ilieinc.dontsleep.ui.model.common.SavedTime
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

    protected val _state = MutableStateFlow(
        CardUiState(
            isLoading = true
        )
    )
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch(ioScope) {
            context.dataStore.getValue(statePreferenceKey, "").let { stateJson ->
                if (stateJson.isNotEmpty()) {
                    runCatching {
                        _state.update { it.copy(isLoading = true) }
                        val timeoutState = Gson().fromJson(stateJson, CardUiState::class.java).let {
                            it.copy(
                                clockState = it.clockState.copy(
                                    savedTimes = it.clockState.savedTimes.toSortedSet(SavedTime.Comparator)
                                )
                            )
                        }
                        withContext(Dispatchers.Main) {
                            _state.update { timeoutState }
                        }
                    }.onFailure {
                        Logger.error("Failed to parse saved state", it)
                        _state.update {
                            CardUiState(isLoading = false)
                        }
                    }.onSuccess {
                        _state.update { it.copy(isLoading = false) }
                    }
                }
            }
            serviceRunning.collect { serviceRunning ->
                _state.update {
                    it.copy(enabled = serviceRunning)
                }
            }
        }.invokeOnCompletion { exception ->
            if (exception != null) {
                Logger.error("Failed to initialize CardViewModel", exception)
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
            is OnConfirmEditClick -> onConfirmEditClick(event.hour, event.minute, event.editMode)
            is OnEditSavedTimeClick -> onEditSavedTimeClick()
            OnDeleteButtonClick -> onDeleteButtonClick()
            is OnExpandedDropdownChanged -> onExpandedDropdownChanged(event.expanded)
            is OnSavedTimeSelectionChange -> onSelectionChange(event.savedTime)
            is OnSwitchTimePickerModeButtonClick -> onSwitchTimePickerModeButtonClick(event.timepickerMode)
        }
    }

    protected fun updateTitle(title: String) {
        _state.update { it.copy(title = title) }
    }

    private fun updateTimeout(hours: Int, minutes: Int) {
        _state.update {
            it.copy(
                timeoutState = it.timeoutState.copy(
                    selectedTime = SavedTime(hours, minutes)
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
                },
                clockState = state.clockState.copy(
                    editMode = null
                ),
            ).also(::updateState)
        }
    }

    private fun onAddButtonClick() {
        _state.update {
            it.copy(
                clockState = it.clockState.copy(
                    editMode = EditMode.ADD
                )
            )
        }
    }

    private fun onCancelButtonClick() {
        _state.update {
            it.copy(
                clockState = it.clockState.copy(
                    editMode = null
                )
            )
        }
    }

    private fun onEditSavedTimeClick() {
        _state.update {
            it.copy(
                clockState = it.clockState.copy(
                    editMode = EditMode.EDIT
                )
            )
        }
    }

    private fun onConfirmEditClick(hours: Int, minutes: Int, editMode: EditMode) {
        val newTime = SavedTime(hours, minutes)
        _state.update {
            it.copy(
                clockState = it.clockState.copy(
                    editMode = null,
                    selectedTime = newTime,
                    savedTimes = it.clockState.savedTimes.toMutableSet().apply {
                        if (editMode == EditMode.EDIT) {
                            remove(it.clockState.selectedTime)
                        }
                        add(newTime)
                    }.toSortedSet(SavedTime.Comparator)
                )
            ).also(::updateState)
        }
    }

    private fun onDeleteButtonClick() {
        _state.update {
            val updatedItems = it.clockState.savedTimes.toMutableSet().apply {
                remove(state.value.clockState.selectedTime)
            }.toSortedSet(SavedTime.Comparator)
            it.copy(
                clockState = it.clockState.copy(
                    selectedTime = updatedItems.firstOrNull(),
                    editMode = null,
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

    private fun onSelectionChange(savedTime: SavedTime) {
        _state.update {
            it.copy(
                clockState = it.clockState.copy(
                    selectedTime = savedTime,
                    isDropdownExpanded = false
                )
            ).also(::updateState)
        }
    }

    private fun onSwitchTimePickerModeButtonClick(timepickerMode: TimepickerMode) {
        _state.update {
            it.copy(
                clockState = it.clockState.copy(
                    timepickerMode = when (timepickerMode) {
                        TimepickerMode.DIGITAL_INPUT -> TimepickerMode.CLOCK_PICKER
                        TimepickerMode.CLOCK_PICKER -> TimepickerMode.DIGITAL_INPUT
                    }
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
