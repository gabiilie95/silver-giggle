package com.ilieinc.dontsleep.manager

import android.app.Notification
import android.content.pm.ServiceInfo
import android.os.Build
import android.text.format.DateFormat
import com.ilieinc.dontsleep.R
import com.ilieinc.dontsleep.data.DontSleepDataStore.MEDIA_STATE_PREF_KEY
import com.ilieinc.dontsleep.service.MediaTimeoutService
import com.ilieinc.dontsleep.util.DontSleepNotificationManager

class MediaTimeoutServiceManager(
    serviceClass: Class<*>,
    serviceTag: String,
    serviceId: Int
) : BaseServiceManager(
    serviceClass = serviceClass,
    serviceTaskTag = serviceTag,
    serviceStatePreferenceKey = MEDIA_STATE_PREF_KEY,
    serviceId = serviceId
) {
    override val foregroundServiceType =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
        } else {
            FOREGROUND_SERVICE_TYPE_UNSPECIFIED
        }

    override fun buildNotification(): Notification =
        DontSleepNotificationManager.createTimeoutNotification<MediaTimeoutService>(
            context,
            R.drawable.baseline_timer_24,
            context.getString(R.string.app_name),
            // Null until the persisted state has loaded. Showing no time beats showing one
            // that is only a placeholder.
            timeoutDateTime?.let { stopTime ->
                context.getString(
                    R.string.media_timeout_notification_text,
                    DateFormat.getTimeFormat(context).format(stopTime.time)
                )
            } ?: context.getString(R.string.media_timeout_notification_indefinite_text)
        )
}