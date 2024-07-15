package com.ilieinc.dontsleep.manager

import android.app.Notification
import android.content.pm.ServiceInfo
import android.os.Build
import android.text.format.DateFormat
import com.ilieinc.dontsleep.R
import com.ilieinc.dontsleep.data.DontSleepDataStore.MEDIA_TIMEOUT_ENABLED_PREF_KEY
import com.ilieinc.dontsleep.data.DontSleepDataStore.MEDIA_TIMEOUT_PREF_KEY
import com.ilieinc.dontsleep.service.MediaTimeoutService
import com.ilieinc.dontsleep.util.DontSleepNotificationManager

class MediaTimeoutServiceManager(
    serviceClass: Class<*>,
    serviceTag: String,
    serviceId: Int
) : BaseServiceManager(
    serviceClass = serviceClass,
    serviceTaskTag = serviceTag,
    serviceTimeoutPreferenceKey = MEDIA_TIMEOUT_PREF_KEY,
    serviceEnabledPreferenceKey = MEDIA_TIMEOUT_ENABLED_PREF_KEY,
    serviceId = serviceId
) {
    override val foregroundServiceTypeFlag by lazy {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_NONE
            }

            else -> null
        }
    }

    override val notification: Notification by lazy {
        DontSleepNotificationManager.createTimeoutNotification<MediaTimeoutService>(
            context,
            R.drawable.baseline_timer_24,
            context.getString(R.string.app_name),
            context.getString(
                R.string.media_timeout_notification_text,
                DateFormat.getTimeFormat(context).format(timeoutDateTime.time)
            )
        )
    }
}