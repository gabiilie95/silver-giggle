package com.ilieinc.dontsleep.ui.model

import com.ilieinc.dontsleep.ui.model.common.ClockState
import com.ilieinc.dontsleep.ui.model.common.SavedTime
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
    val statusButtonEnabled get() = enabled || !timeoutEnabled || when (timeoutMode) {
        TimeoutMode.CLOCK -> isSelectedTimeValid(clockState.selectedTime)
        TimeoutMode.TIMEOUT -> isSelectedTimeValid(timeoutState.selectedTime)
    }

    val editControlsEnabled get() = !enabled

    val selectedTime
        get() = when (timeoutMode) {
            TimeoutMode.TIMEOUT -> timeoutState.selectedTime
            TimeoutMode.CLOCK -> clockState.selectedTime
        }

    private fun isSelectedTimeValid(selectedTime: SavedTime?) = with(selectedTime) {
        this != null && (hour > 0 || minute > 0)
    }

    enum class TimeoutMode {
        TIMEOUT,
        CLOCK
    }
}
