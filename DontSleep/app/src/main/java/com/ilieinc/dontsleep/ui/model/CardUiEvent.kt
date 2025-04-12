package com.ilieinc.dontsleep.ui.model

import com.ilieinc.dontsleep.ui.model.common.SelectedTime

sealed interface CardUiEvent {
    data class OnTimeoutTimeChange(val hours: Int, val minutes: Int) : CardUiEvent
    data class OnStatusToggleChange(val enabled: Boolean) : CardUiEvent
    data class OnAutoOffToggleChange(val enabled: Boolean) : CardUiEvent
    data class OnChangeHelpDialogVisibility(val visible: Boolean) : CardUiEvent
    data class OnChangePermissionDialogVisibility(val visible: Boolean) : CardUiEvent
    data class OnTimeoutModeButtonClick(val state: CardUiState) : CardUiEvent
    data class OnSaveButtonClick(val hour: Int, val minute: Int, val isAfternoon: Boolean) : CardUiEvent
    object OnAddButtonClick : CardUiEvent
    object OnCancelButtonClick : CardUiEvent
    object OnDeleteButtonClick : CardUiEvent
    data class OnExpandedDropdownChanged(val expanded: Boolean) : CardUiEvent
    data class OnSavedTimeSelectionChange(val savedTime: SelectedTime) : CardUiEvent
}
