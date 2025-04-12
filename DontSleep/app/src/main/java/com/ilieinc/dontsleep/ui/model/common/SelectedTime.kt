package com.ilieinc.dontsleep.ui.model.common

data class SelectedTime(
    val hour: Int,
    val minute: Int
) {
    val isAfternoon: Boolean
        get() = hour >= 12
    val formattedTime: String
        get() = "%02d:%02d %s".format(
            with(hour) {
                if (isAfternoon && this != 12) {
                    this - 12
                } else {
                    this
                }
            }, minute, if (isAfternoon) {
                "PM"
            } else {
                "AM"
            }
        )
}