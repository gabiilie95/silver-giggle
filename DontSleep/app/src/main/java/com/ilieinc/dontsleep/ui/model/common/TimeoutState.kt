package com.ilieinc.dontsleep.ui.model.common

data class TimeoutState(
    val selectedTime: SavedTime = SavedTime(
        hour = 0,
        minute = 0
    )
)
