package com.ilieinc.dontsleep.ui.model.common

import java.util.UUID

data class SavedTime(
    val hour: Int,
    val minute: Int,
    val isFavorite: Boolean = false
) {
    val isAfternoon: Boolean
        get() = hour >= 12

    fun getFormattedTime(is24Hour: Boolean) = if (is24Hour) {
        "%02d:%02d".format(hour, minute)
    } else {
        "%02d:%02d %s".format(
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

    object Comparator : kotlin.Comparator<SavedTime> {
        override fun compare(o1: SavedTime?, o2: SavedTime?): Int {
            if (o1 == null && o2 == null) return 0
            if (o1 == null) return -1
            if (o2 == null) return 1

            val o1Minutes = o1.hour * 60 + o1.minute
            val o2Minutes = o2.hour * 60 + o2.minute
            return o1Minutes.compareTo(o2Minutes)
        }
    }
}