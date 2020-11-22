package com.ilieinc.dontsleep.service

import android.content.Context
import com.ilieinc.dontsleep.timer.LockScreenWorker
import com.ilieinc.dontsleep.timer.TimerManager
import com.ilieinc.dontsleep.util.NotificationManager
import com.ilieinc.dontsleep.util.StateHelper

class SleepService : BaseService(
    SleepService::class.java,
    SLEEP_TAG,
    2
) {
    companion object {
        var serviceStatusChangedCallback: ((serviceName: String, enabled: Boolean) -> Unit)? = null
        const val SLEEP_TAG = "DontSleep::SleepTag"
        const val SLEEP_SERVICE_STOP_TAG = "DontSleep::SleepServiceStopTag"
        fun isRunning(context: Context) =
            StateHelper.isServiceRunning(context, SleepService::class.java)

        fun cancelLockWorker(context: Context) {
            TimerManager.cancelTask(context, SLEEP_SERVICE_STOP_TAG)
        }
    }

    override var serviceChangedCallback: ((serviceName: String, enabled: Boolean) -> Unit)? = null
        get() = serviceStatusChangedCallback

    override fun initFields() {
        notification = NotificationManager.createScreenSleepNotification(this)
    }

    override fun onCreate() {
        super.onCreate()
        TimerManager.setTimedTask<LockScreenWorker>(
            this,
            timeoutDateTime.time,
            SLEEP_SERVICE_STOP_TAG
        )
    }
}