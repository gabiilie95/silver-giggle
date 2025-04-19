package com.ilieinc.dontsleep.ui.model.common

data class SavedTime(
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