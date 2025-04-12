package com.ilieinc.dontsleep.ui.model.common

data class TimeoutState(
    val selectedTime: SelectedTime = SelectedTime(
        hour = 0,
        minute = 0
    )
)
