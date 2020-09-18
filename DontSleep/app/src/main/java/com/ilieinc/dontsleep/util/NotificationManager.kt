package com.ilieinc.dontsleep.util


import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.ilieinc.dontsleep.MainActivity
import com.ilieinc.dontsleep.R
import com.ilieinc.dontsleep.service.SleepService
import com.ilieinc.dontsleep.service.SleepService.Companion.SLEEP_TAG
import com.ilieinc.dontsleep.service.TimeoutService
import java.lang.reflect.Method
import java.util.*

object NotificationManager {

    fun createScreenTimeoutNotification(context: Context): Notification =
        createStickyNotification<TimeoutService>(context) { builder ->
//            val screenshotServiceIntent = Intent(context, ScreenshotService::class.java)
//            screenshotServiceIntent.putExtra(FROM_NOTIFICATION_EXTRA, true)
//            val screenshotServicePendingIntent =
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    PendingIntent.getForegroundService(
//                        context,
//                        0,
//                        screenshotServiceIntent,
//                        PendingIntent.FLAG_UPDATE_CURRENT
//                    )
//                } else {
//                    PendingIntent.getService(
//                        context,
//                        0,
//                        screenshotServiceIntent,
//                        PendingIntent.FLAG_UPDATE_CURRENT
//                    )
//                }
            val mainActivityIntent = Intent(context, MainActivity::class.java)
            val mainActivityPendingIntent = PendingIntent.getActivity(
                context,
                1,
                mainActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            builder
                .setSmallIcon(R.drawable.baseline_mobile_friendly_24)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.notification_text))
                .setContentIntent(mainActivityPendingIntent)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                .addAction(
//                    R.mipmap.ic_close,
//                    context.getString(R.string.close),
//                    killAppPendingIntent
//                )
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        }

    fun createScreenSleepNotification(context: Context): Notification =
        createStickyNotification<SleepService>(context) { builder ->
            val mainActivityIntent = Intent(context, MainActivity::class.java)
            val mainActivityPendingIntent = PendingIntent.getActivity(
                context,
                1,
                mainActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            val timeout = Calendar.getInstance().add(
                Calendar.MILLISECOND,
                SharedPreferenceManager.getInstance(context).getLong(SLEEP_TAG, 900000)
                    .toInt()
            )
            builder
                .setSmallIcon(R.drawable.baseline_timer_24)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText("Turning screen off at $timeout")
                .setContentIntent(mainActivityPendingIntent)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        }

    @SuppressLint("WrongConstant")
    fun tryCloseNotificationBar(context: Context) {
        try {
            val statusBarService: Any = context.getSystemService("statusbar")
            val statusBarManager = Class.forName("android.app.StatusBarManager")
            val hideStatusBar: Method = statusBarManager.getMethod("collapsePanels")
            hideStatusBar.invoke(statusBarService)
        } catch (ex: Exception) {
            Logger.error(ex)
        }
    }

    inline fun <reified T> createStickyNotification(
        context: Context,
        @SuppressLint("InlinedApi")
        importance: Int = NotificationManager.IMPORTANCE_DEFAULT,
        builderActions: (NotificationCompat.Builder) -> Unit
    ): Notification {
        val builder =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationChannel = createNotificationChannel<T>(context, importance)
                NotificationCompat.Builder(context, notificationChannel.id)
            } else {
                NotificationCompat.Builder(context)
            }
        builderActions(builder)
        return builder.build()
    }

    // This is needed for Android 8+
    @RequiresApi(Build.VERSION_CODES.O)
    inline fun <reified T> createNotificationChannel(
        context: Context,
        importance: Int = NotificationManager.IMPORTANCE_DEFAULT
    ): NotificationChannel {
        val notificationChannel = NotificationChannel(
            "${T::class.qualifiedName}NotificationChannel",
            "${T::class.simpleName} Notification",
            importance
        )
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
        return notificationChannel
    }
}