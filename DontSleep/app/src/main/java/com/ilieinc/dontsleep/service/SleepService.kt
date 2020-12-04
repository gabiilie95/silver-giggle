package com.ilieinc.dontsleep.service

import android.content.Context
import android.text.format.DateFormat
import com.ilieinc.dontsleep.R
import com.ilieinc.dontsleep.model.ServiceStatusChangedEvent
import com.ilieinc.dontsleep.timer.LockScreenWorker
import com.ilieinc.dontsleep.timer.TimerManager
import com.ilieinc.dontsleep.util.NotificationManager
import com.ilieinc.dontsleep.util.SharedPreferenceManager
import com.ilieinc.dontsleep.util.StateHelper
import com.ilieinc.kotlinevents.Event
import java.util.*

class SleepService : BaseService(
    SleepService::class.java,
    SLEEP_TAG,
    2
) {
    companion object {
        val serviceStatusChanged = Event(ServiceStatusChangedEvent::class.java)
        const val SLEEP_TAG = "DontSleep::SleepTag"
        const val SLEEP_SERVICE_STOP_TAG = "DontSleep::SleepServiceStopTag"
        fun isRunning(context: Context) =
            StateHelper.isServiceRunning(context, SleepService::class.java)
    }

    override val serviceStatusChanged: Event<ServiceStatusChangedEvent> =
        Companion.serviceStatusChanged

    override fun initFields() {
        notification = NotificationManager.createTimeoutNotification<SleepService>(
            this,
            R.drawable.baseline_timer_24,
            getString(R.string.app_name),
            getString(
                R.string.sleep_notification_text,
                DateFormat.getTimeFormat(this).format(Calendar.getInstance().apply {
                    add(
                        Calendar.MILLISECOND,
                        SharedPreferenceManager.getInstance(this@SleepService)
                            .getLong(SLEEP_TAG, 900000)
                            .toInt()
                    )
                }.time)
            )
        )
    }

    override fun onCreate() {
        super.onCreate()
        TimerManager.setTimedTask<LockScreenWorker>(
            this,
            timeoutDateTime.time,
            SLEEP_SERVICE_STOP_TAG
        )
    }

    override fun onDestroy() {
        TimerManager.cancelTask(this, SLEEP_SERVICE_STOP_TAG)
        super.onDestroy()
    }
}