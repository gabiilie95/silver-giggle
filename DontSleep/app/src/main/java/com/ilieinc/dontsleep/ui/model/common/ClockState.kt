package com.ilieinc.dontsleep.ui.model.common

data class ClockState(
    val selectedTime: SavedTime? = null,
    @Transient val editMode: EditMode? = null,
    val timepickerMode: TimepickerMode = TimepickerMode.DIGITAL_INPUT,
    val is24hour: Boolean = false,
    val isDropdownExpanded: Boolean = false,
    val savedTimes: Set<SavedTime> = emptySet()
) {
    enum class EditMode {
        ADD,
        EDIT
    }

    enum class TimepickerMode {
        DIGITAL_INPUT,
        CLOCK_PICKER
    }
}
