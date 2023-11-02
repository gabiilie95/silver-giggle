package com.ilieinc.dontsleep.service

import android.content.Context
import android.text.format.DateFormat
import com.ilieinc.dontsleep.R
import com.ilieinc.dontsleep.timer.LockScreenWorker
import com.ilieinc.dontsleep.timer.TimerManager
import com.ilieinc.core.util.SharedPreferenceManager
import com.ilieinc.core.util.StateHelper
import com.ilieinc.dontsleep.util.DontSleepNotificationManager
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.*

class ScreenTimeoutService : BaseService(
    ScreenTimeoutService::class.java,
    SLEEP_TAG,
    2
) {
    companion object {
        const val SLEEP_TAG = "DontSleep::ScreenTimeoutTag"
        const val SLEEP_SERVICE_STOP_TAG = "DontSleep::ScreenTimeoutServiceStopTag"
        const val TIMEOUT_ENABLED_TAG = "${SLEEP_TAG}_TimeoutEnabled"

        fun isRunning(context: Context) =
            StateHelper.isServiceRunning(context, ScreenTimeoutService::class.java)
        val serviceRunning = MutableStateFlow(false)
    }

    override val binder: ServiceBinder = ServiceBinder(this)

    override fun initFields() {
        notification = DontSleepNotificationManager.createTimeoutNotification<ScreenTimeoutService>(
            this,
            R.drawable.baseline_timer_24,
            getString(R.string.app_name),
            getString(
                R.string.sleep_notification_text,
                DateFormat.getTimeFormat(this).format(Calendar.getInstance().apply {
                    add(
                        Calendar.MILLISECOND,
                        SharedPreferenceManager.getInstance(this@ScreenTimeoutService)
                            .getLong(SLEEP_TAG, 900000)
                            .toInt()
                    )
                }.time)
            )
        )
        timeoutEnabled =
            SharedPreferenceManager.getInstance(this).getBoolean(TIMEOUT_ENABLED_TAG, true)
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