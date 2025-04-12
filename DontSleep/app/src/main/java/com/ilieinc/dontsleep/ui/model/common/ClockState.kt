package com.ilieinc.dontsleep.ui.model.common

data class ClockState(
    val selectedTime: SelectedTime? = null,
    @Transient
    val isAddingNewTime: Boolean = false,
    val isDropdownExpanded: Boolean = false,
    val savedTimes: List<SelectedTime> = emptyList()
)
