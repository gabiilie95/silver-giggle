package com.ilieinc.dontsleep.service

import android.content.Context
import android.text.format.DateFormat
import com.ilieinc.dontsleep.R
import com.ilieinc.dontsleep.timer.LockScreenWorker
import com.ilieinc.dontsleep.timer.TimerManager
import com.ilieinc.dontsleep.util.NotificationManager
import com.ilieinc.dontsleep.util.SharedPreferenceManager
import com.ilieinc.dontsleep.util.StateHelper
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.*

class SleepService : BaseService(
    SleepService::class.java,
    SLEEP_TAG,
    2
) {
    companion object {
        const val SLEEP_TAG = "DontSleep::SleepTag"
        const val SLEEP_SERVICE_STOP_TAG = "DontSleep::SleepServiceStopTag"
        fun isRunning(context: Context) =
            StateHelper.isServiceRunning(context, SleepService::class.java)
        val serviceRunning = MutableStateFlow(false)
    }

    override val binder: ServiceBinder = ServiceBinder(this)

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
        serviceRunning.tryEmit(true)
        TimerManager.setTimedTask<LockScreenWorker>(
            this,
            timeoutDateTime.time,
            SLEEP_SERVICE_STOP_TAG
        )
    }

    override fun onDestroy() {
        TimerManager.cancelTask(this, SLEEP_SERVICE_STOP_TAG)
        serviceRunning.tryEmit(false)
        super.onDestroy()
    }
}