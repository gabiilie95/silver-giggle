package com.ilieinc.dontsleep.ui.model.common

data class TimeoutState(
    val selectedTime: SavedTime = SavedTime(
        hour = 0,
        minute = DEFAULT_TIME_MINS
    )
)

private const val DEFAULT_TIME_MINS = 15