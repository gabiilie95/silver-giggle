package com.ilieinc.dontsleep.ui.model.common

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ClockState(
    val selectedTime: SavedTime? = null,
    @Transient val editMode: EditMode? = null,
    val timepickerMode: TimepickerMode = TimepickerMode.DIGITAL_INPUT,
    val is24hour: Boolean = false,
    val isDropdownExpanded: Boolean = false,
    val savedTimes: List<SavedTime> = emptyList()
) {
    @Serializable
    enum class EditMode {
        ADD,
        EDIT
    }

    @Serializable
    enum class TimepickerMode {
        DIGITAL_INPUT,
        CLOCK_PICKER
    }
}
