package com.ilieinc.dontsleep.manager

import android.app.Notification
import android.content.pm.ServiceInfo
import android.os.Build
import android.text.format.DateFormat
import com.ilieinc.dontsleep.R
import com.ilieinc.dontsleep.data.DontSleepDataStore.WAKE_LOCK_TIMEOUT_ENABLED_PREF_KEY
import com.ilieinc.dontsleep.data.DontSleepDataStore.WAKE_LOCK_TIMEOUT_PREF_KEY
import com.ilieinc.dontsleep.service.WakeLockService
import com.ilieinc.dontsleep.util.DontSleepNotificationManager

class WakeLockServiceManager(
    serviceClass: Class<*>,
    serviceTag: String,
    serviceId: Int
) : BaseServiceManager(
    serviceClass = serviceClass,
    serviceTaskTag = serviceTag,
    serviceTimeoutPreferenceKey = WAKE_LOCK_TIMEOUT_PREF_KEY,
    serviceEnabledPreferenceKey = WAKE_LOCK_TIMEOUT_ENABLED_PREF_KEY,
    serviceId = serviceId
) {
    override val foregroundServiceTypeFlag by lazy {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_NONE
            }

            else -> null
        }
    }

    override val notification: Notification by lazy {
        DontSleepNotificationManager.createTimeoutNotification<WakeLockService>(
            context,
            R.drawable.baseline_mobile_friendly_24,
            context.getString(R.string.app_name),
            if (timeoutEnabled) {
                context.getString(
                    R.string.timeout_notification_text,
                    DateFormat.getTimeFormat(context).format(timeoutDateTime.time)
                )
            } else {
                context.getString(R.string.timeout_notification_indefinite_text)
            }
        )
    }
}