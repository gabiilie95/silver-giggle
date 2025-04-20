package com.ilieinc.dontsleep.ui.model

import com.ilieinc.dontsleep.ui.model.common.ClockState
import com.ilieinc.dontsleep.ui.model.common.SavedTime

sealed interface CardUiEvent {
    data class OnTimeoutTimeChange(val hours: Int, val minutes: Int) : CardUiEvent
    data class OnStatusToggleChange(val enabled: Boolean) : CardUiEvent
    data class OnAutoOffToggleChange(val enabled: Boolean) : CardUiEvent
    data class OnChangeHelpDialogVisibility(val visible: Boolean) : CardUiEvent
    data class OnChangePermissionDialogVisibility(val visible: Boolean) : CardUiEvent
    data class OnTimeoutModeButtonClick(val state: CardUiState) : CardUiEvent
    data class OnConfirmEditClick(val hour: Int, val minute: Int, val isFavorite: Boolean, val editMode: ClockState.EditMode) : CardUiEvent
    object OnAddButtonClick : CardUiEvent
    object OnCancelButtonClick : CardUiEvent
    object OnEditSavedTimeClick : CardUiEvent
    object OnDeleteButtonClick : CardUiEvent
    data class OnExpandedDropdownChanged(val expanded: Boolean) : CardUiEvent
    data class OnSavedTimeSelectionChange(val savedTime: SavedTime) : CardUiEvent
    data class OnSwitchTimePickerModeButtonClick(val timepickerMode: ClockState.TimepickerMode) : CardUiEvent
    data class On24HourModeChange(val is24hour: Boolean) : CardUiEvent
    data class OnItemFavoriteClick(val savedTime: SavedTime) : CardUiEvent
    data class OnFavoriteItemStartClick(val savedTime: SavedTime) : CardUiEvent
}
