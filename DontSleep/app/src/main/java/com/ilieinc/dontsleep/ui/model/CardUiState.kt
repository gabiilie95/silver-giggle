package com.ilieinc.dontsleep.ui.model

import com.ilieinc.dontsleep.ui.model.common.ClockState
import com.ilieinc.dontsleep.ui.model.common.TimeoutState

data class CardUiState(
    val title: String = "",
    val timeoutState: TimeoutState = TimeoutState(),
    val clockState: ClockState = ClockState(),
    val timeoutMode: TimeoutMode = TimeoutMode.TIMEOUT,
    val timeoutEnabled: Boolean = true,
    @Transient val isLoading: Boolean = false,
    @Transient val enabled: Boolean = false,
    @Transient val showTimeoutSectionToggle: Boolean = true,
    @Transient val showPermissionDialog: Boolean = false,
    @Transient val showHelpDialog: Boolean = false,
    @Transient val permissionRequired: Boolean = false
) {
    val enableButtonEnabled get() = enabled || when (timeoutMode) {
        TimeoutMode.CLOCK -> clockState.selectedTime != null
        else -> true
    }
    val editControlsEnabled get() = !enabled
    val selectedTime
        get() = when (timeoutMode) {
            TimeoutMode.TIMEOUT -> timeoutState.selectedTime
            TimeoutMode.CLOCK -> clockState.selectedTime
        }

    enum class TimeoutMode {
        TIMEOUT,
        CLOCK
    }
}
