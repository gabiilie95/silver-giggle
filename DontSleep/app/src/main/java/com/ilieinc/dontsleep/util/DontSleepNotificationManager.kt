package com.ilieinc.dontsleep.util

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.ilieinc.dontsleep.service.BaseService
import com.ilieinc.core.util.NotificationManager
import com.ilieinc.dontsleep.MainActivity
import com.ilieinc.dontsleep.R
import com.ilieinc.dontsleep.service.WakeLockService

object DontSleepNotificationManager {
    const val STOP_COMMAND = "Stop"

    inline fun <reified T> createTimeoutNotification(
        context: Context,
        smallIcon: Int,
        title: String,
        text: String
    ): Notification where T : BaseService =
        NotificationManager.createStickyNotification<WakeLockService>(
            context,
            NotificationManagerCompat.IMPORTANCE_MIN
        ) { builder ->
            val stopServiceIntent = Intent(context, T::class.java)
            stopServiceIntent.putExtra(STOP_COMMAND, true)
            val stopServicePendingIntent =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    PendingIntent.getForegroundService(
                        context,
                        0,
                        stopServiceIntent,
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    )
                } else {
                    PendingIntent.getService(
                        context,
                        0,
                        stopServiceIntent,
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    )
                }
            val mainActivityIntent = Intent(context, MainActivity::class.java)
            val mainActivityPendingIntent = PendingIntent.getActivity(
                context,
                1,
                mainActivityIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            builder
                .setSmallIcon(smallIcon)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(mainActivityPendingIntent)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .addAction(
                    0,
                    context.getString(R.string.stop),
                    stopServicePendingIntent
                )
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        }
}