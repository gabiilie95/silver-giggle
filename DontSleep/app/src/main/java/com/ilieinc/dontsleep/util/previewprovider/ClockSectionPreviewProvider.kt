package com.ilieinc.dontsleep.util.previewprovider

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.ilieinc.dontsleep.ui.model.CardUiState
import com.ilieinc.dontsleep.ui.model.common.ClockState
import com.ilieinc.dontsleep.ui.model.common.ClockState.EditMode
import com.ilieinc.dontsleep.ui.model.common.SavedTime

class ClockSectionPreviewProvider : PreviewParameterProvider<CardUiState> {
    override val values: Sequence<CardUiState>
        get() = sequenceOf(
            CardUiState(
                clockState = ClockState(
                    savedTimes = sortedSetOf(
                        SavedTime(1, 0),
                        SavedTime(2, 30),
                        SavedTime(3, 45)
                    ),
                    selectedTime = SavedTime(2, 30),
                    editMode = EditMode.EDIT
                ),
                enabled = true
            ),
            CardUiState(
                clockState = ClockState(
                    savedTimes = sortedSetOf(
                        SavedTime(1, 0),
                        SavedTime(2, 30),
                        SavedTime(3, 45)
                    ),
                    selectedTime = SavedTime(2, 30),
                    editMode = EditMode.EDIT,
                    timepickerMode = ClockState.TimepickerMode.CLOCK_PICKER
                ),
                enabled = true
            )
        )
}