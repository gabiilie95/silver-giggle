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
import com.ilieinc.dontsleep.service.BaseService
import com.ilieinc.dontsleep.service.TimeoutService
import java.lang.reflect.Method

object NotificationManager {
    const val STOP_COMMAND = "Stop"

    inline fun <reified T> createTimeoutNotification(
        context: Context,
        smallIcon: Int,
        title: String,
        text: String
    ): Notification where T : BaseService =
        createStickyNotification<TimeoutService>(context) { builder ->
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
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .addAction(
                    0,
                    context.getString(R.string.stop),
                    stopServicePendingIntent
                )
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