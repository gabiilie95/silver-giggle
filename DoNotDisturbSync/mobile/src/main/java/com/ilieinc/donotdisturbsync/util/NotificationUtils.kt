package com.ilieinc.donotdisturbsync.util

import android.app.Application
import android.app.NotificationManager
import android.app.NotificationManager.INTERRUPTION_FILTER_ALL
import android.app.NotificationManager.INTERRUPTION_FILTER_NONE
import android.content.Context


class NotificationUtils(val application: Application) {

    fun changeDoNotDisturbStatus(enabled: Boolean){
        val notificationManager =
            application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        kotlin.runCatching {
            notificationManager.setInterruptionFilter(
                if (enabled) {
                    INTERRUPTION_FILTER_NONE
                } else {
                    INTERRUPTION_FILTER_ALL
                }
            )
        }.onFailure { ex ->
            Logger.error(ex)
        }
    }
}