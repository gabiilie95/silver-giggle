package com.ilieinc.dontsleep.ui.model

sealed interface CardUiEvent {
    data class TimeoutTimeChanged(val hours: Int, val minutes: Int) : CardUiEvent
    data class StatusToggleChanged(val enabled: Boolean) : CardUiEvent
    data class AutoOffToggleChanged(val enabled: Boolean) : CardUiEvent
    data class ChangeHelpDialogVisibility(val visible: Boolean) : CardUiEvent
    data class ChangePermissionDialogVisibility(val visible: Boolean) : CardUiEvent
}