package com.ilieinc.core.util

import android.annotation.SuppressLint
import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.lang.reflect.Method

object NotificationManager {

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
        importance: Int = NotificationManagerCompat.IMPORTANCE_DEFAULT,
        builderActions: (NotificationCompat.Builder) -> Unit
    ): Notification {
        val notificationChannel = createNotificationChannel<T>(context, importance)
        val builder = NotificationCompat.Builder(context, notificationChannel.id)
        builderActions(builder)
        return builder.build()
    }

    inline fun <reified T> createNotificationChannel(
        context: Context,
        importance: Int = NotificationManagerCompat.IMPORTANCE_DEFAULT
    ): NotificationChannelCompat {
        val notificationChannel = with(
            NotificationChannelCompat.Builder(
                "${T::class.qualifiedName}NotificationChannel",
                importance
            )
        ) {
            setName("${T::class.simpleName} Notification")
            build()
        }
        NotificationManagerCompat.from(context).createNotificationChannel(notificationChannel)
        return notificationChannel
    }
}