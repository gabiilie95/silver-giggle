package com.ilieinc.dontsleep.util.previewprovider

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.ilieinc.dontsleep.ui.model.CardUiState
import com.ilieinc.dontsleep.ui.model.common.ClockState
import com.ilieinc.dontsleep.ui.model.common.ClockState.EditMode
import com.ilieinc.dontsleep.ui.model.common.SavedTime
import com.ilieinc.dontsleep.ui.model.common.TimeoutState


class TimePickerPreviewProvider : PreviewParameterProvider<CardUiState> {
    override val values: Sequence<CardUiState>
        get() = sequenceOf(
            CardUiState(
                title = "Loading",
                isLoading = true,
                timeoutEnabled = true,
                enabled = true,
                showTimeoutSectionToggle = true,
                showPermissionDialog = false,
                showHelpDialog = false,
                permissionRequired = false,
                clockState = ClockState(),
            ),
            CardUiState(
                title = "Clock",
                clockState = ClockState(
                    editMode = EditMode.ADD,
                    selectedTime = SavedTime(1, 30),
                    savedTimes = listOf(
                        SavedTime(1, 30),
                        SavedTime(2, 45),
                        SavedTime(3, 15),
                        SavedTime(4, 0),
                        SavedTime(5, 30)
                    )
                ),
                timeoutEnabled = true,
                enabled = true,
                showTimeoutSectionToggle = true,
                showPermissionDialog = false,
                showHelpDialog = false,
                permissionRequired = false,
                timeoutMode = CardUiState.TimeoutMode.CLOCK
            ),
            CardUiState(
                title = "Clock Edit Mode",
                clockState = ClockState(
                    editMode = EditMode.EDIT,
                    selectedTime = SavedTime(1, 30),
                    savedTimes = listOf(
                        SavedTime(1, 30),
                        SavedTime(2, 45),
                        SavedTime(3, 15),
                        SavedTime(4, 0),
                        SavedTime(5, 30)
                    )
                ),
                timeoutEnabled = true,
                enabled = true,
                showTimeoutSectionToggle = true,
                showPermissionDialog = false,
                showHelpDialog = false,
                permissionRequired = false,
                timeoutMode = CardUiState.TimeoutMode.CLOCK
            ),
            CardUiState(
                title = "Saved Time",
                clockState = ClockState(
                    selectedTime = SavedTime(1, 30),
                    savedTimes = listOf(
                        SavedTime(1, 30),
                        SavedTime(2, 45),
                        SavedTime(3, 15),
                        SavedTime(4, 0),
                        SavedTime(5, 30)
                    )
                ),
                timeoutEnabled = true,
                enabled = true,
                showTimeoutSectionToggle = true,
                showPermissionDialog = false,
                showHelpDialog = false,
                permissionRequired = false,
                timeoutMode = CardUiState.TimeoutMode.CLOCK
            ),
            CardUiState(
                title = "Timeout",
                timeoutState = TimeoutState(
                    selectedTime = SavedTime(1, 30)
                ),
                timeoutEnabled = true,
                enabled = true,
                showTimeoutSectionToggle = true,
                showPermissionDialog = false,
                showHelpDialog = false,
                permissionRequired = false,
                timeoutMode = CardUiState.TimeoutMode.TIMEOUT
            )
        )
}
