package com.ilieinc.dontsleep.util.previewprovider

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.ilieinc.dontsleep.ui.model.CardUiState
import com.ilieinc.dontsleep.ui.model.common.ClockState
import com.ilieinc.dontsleep.ui.model.common.SelectedTime
import com.ilieinc.dontsleep.ui.model.common.TimeoutState


class ClockPreviewProvider : PreviewParameterProvider<CardUiState> {
    override val values: Sequence<CardUiState>
        get() = sequenceOf(
            CardUiState(
                title = "Clock",
                clockState = ClockState(
                    isAddingNewTime = true,
                    selectedTime = SelectedTime(1, 30),
                    savedTimes = listOf(
                        SelectedTime(1, 30),
                        SelectedTime(2, 45),
                        SelectedTime(3, 15),
                        SelectedTime(4, 0),
                        SelectedTime(5, 30)
                    )
                ),
                timeoutEnabled = true,
                enabled = true,
                showTimeoutSectionToggle = true,
                showPermissionDialog = false,
                showHelpDialog = false,
                permissionRequired = false,
                timeoutMode = CardUiState.TimeoutMode.CLOCK
            )
        )
}

class SavedTimePreviewProvider : PreviewParameterProvider<CardUiState> {
    override val values: Sequence<CardUiState>
        get() = sequenceOf(
            CardUiState(
                title = "Saved Time",
                clockState = ClockState(
                    isAddingNewTime = false,
                    selectedTime = SelectedTime(1, 30),
                    savedTimes = listOf(
                        SelectedTime(1, 30),
                        SelectedTime(2, 45),
                        SelectedTime(3, 15),
                        SelectedTime(4, 0),
                        SelectedTime(5, 30)
                    )
                ),
                timeoutEnabled = true,
                enabled = true,
                showTimeoutSectionToggle = true,
                showPermissionDialog = false,
                showHelpDialog = false,
                permissionRequired = false,
                timeoutMode = CardUiState.TimeoutMode.CLOCK
            )
        )
}

class TimeoutPreviewProvider : PreviewParameterProvider<CardUiState> {
    override val values: Sequence<CardUiState>
        get() = sequenceOf(
            CardUiState(
                title = "Timeout",
                timeoutState = TimeoutState(
                    selectedTime = SelectedTime(1, 30)
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