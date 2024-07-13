package com.ilieinc.dontsleep.service

import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import android.text.format.DateFormat
import com.ilieinc.core.util.SharedPreferenceManager
import com.ilieinc.core.util.StateHelper
import com.ilieinc.dontsleep.R
import com.ilieinc.dontsleep.timer.MediaTimeoutWorker
import com.ilieinc.dontsleep.timer.TimerManager
import com.ilieinc.dontsleep.util.DontSleepNotificationManager
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.*

class MediaTimeoutService : BaseService(
    serviceClass = MediaTimeoutService::class.java,
    serviceTag = MEDIA_TIMEOUT_TAG,
    id = 3,
    foregroundServiceTypeFlag = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
        }

        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_NONE
        }

        else -> null
    }
) {
    companion object {
        const val MEDIA_TIMEOUT_TAG = "DontSleep::MediaTimeoutTag"
        const val MEDIA_TIMEOUT_SERVICE_STOP_TAG = "DontSleep::MediaTimeoutServiceStopTag"
        const val TIMEOUT_ENABLED_TAG = "${MEDIA_TIMEOUT_TAG}_TimeoutEnabled"

        fun isRunning(context: Context) =
            StateHelper.isServiceRunning(context, MediaTimeoutService::class.java)

        val serviceRunning = MutableStateFlow(false)
    }

    override val binder: ServiceBinder = ServiceBinder(this)

    override fun initFields() {
        notification = DontSleepNotificationManager.createTimeoutNotification<MediaTimeoutService>(
            this,
            R.drawable.baseline_timer_24,
            getString(R.string.app_name),
            getString(
                R.string.media_timeout_notification_text,
                DateFormat.getTimeFormat(this).format(Calendar.getInstance().apply {
                    add(
                        Calendar.MILLISECOND,
                        SharedPreferenceManager.getInstance(this@MediaTimeoutService)
                            .getLong(MEDIA_TIMEOUT_TAG, 900000)
                            .toInt()
                    )
                }.time)
            )
        )
    }

    override fun onCreate() {
        super.onCreate()
        serviceRunning.tryEmit(true)
        TimerManager.setTimedTask<MediaTimeoutWorker>(
            this,
            timeoutDateTime.time,
            MEDIA_TIMEOUT_SERVICE_STOP_TAG
        )
    }

    override fun onDestroy() {
        TimerManager.cancelTask(this, MEDIA_TIMEOUT_SERVICE_STOP_TAG)
        serviceRunning.tryEmit(false)
        super.onDestroy()
    }
}