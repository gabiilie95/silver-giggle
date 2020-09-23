package com.ilieinc.dontsleep.service

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.ilieinc.dontsleep.model.ServiceStatusChangedEvent
import com.ilieinc.dontsleep.timer.LockScreenWorker
import com.ilieinc.dontsleep.timer.TimerManager
import com.ilieinc.dontsleep.util.NotificationManager
import com.ilieinc.dontsleep.util.StateHelper
import com.ilieinc.kotlinevents.Event

class SleepService : BaseService(
    SleepService::class.java,
    SLEEP_TAG,
    2
) {
    companion object {
        val serviceStatusChanged = Event(ServiceStatusChangedEvent::class.java)
        const val SLEEP_TAG = "DontSleep::SleepTag"
        fun isRunning(context: Context) =
            StateHelper.isServiceRunning(context, SleepService::class.java)
    }

    override val serviceStatusChanged: Event<ServiceStatusChangedEvent> =
        Companion.serviceStatusChanged

    override fun initFields() {
        notification = NotificationManager.createScreenSleepNotification(this)
    }

    override fun onCreate() {
        super.onCreate()
        ContextCompat.startForegroundService(
            this,
            Intent(this, TimeoutService::class.java)
        )
        TimerManager.setTimedTask<LockScreenWorker>(
            this,
            timeoutDateTime.time,
            SLEEP_TAG
        )
    }

    override fun onDestroy() {
        stopService(Intent(this, TimeoutService::class.java))
        super.onDestroy()
    }
}