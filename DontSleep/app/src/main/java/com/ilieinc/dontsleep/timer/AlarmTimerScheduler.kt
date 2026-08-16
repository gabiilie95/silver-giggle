package com.ilieinc.dontsleep.timer

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import com.ilieinc.core.util.Logger

/**
 * Schedules timed tasks straight through [AlarmManager], for devices where WorkManager
 * cannot be used at all. See [TimerManager], which is the only thing that should call this.
 *
 * Alarms are inexact ([AlarmManager.setAndAllowWhileIdle]) so no exact alarm permission is
 * needed. That matches what WorkManager gives a delayed one-time request anyway, and the
 * screen is on for the whole timeout, which keeps the device out of the deferral windows
 * that would otherwise hold them back.
 */
internal object AlarmTimerScheduler {

    fun schedule(
        context: Context,
        workerClass: Class<*>,
        tag: String,
        delayMillis: Long,
        extras: Map<String, Any>?
    ) {
        runCatching {
            val alarmManager = requireNotNull(
                ContextCompat.getSystemService(context, AlarmManager::class.java)
            ) { "No AlarmManager available" }
            val intent = taskIntent(context, tag).apply {
                putExtra(TimedTaskReceiver.TASK_EXTRA, workerClass.name)
                extras?.let { putExtras(bundleOf(*it.toList().toTypedArray())) }
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode(tag),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + delayMillis,
                pendingIntent
            )
            Logger.info("Scheduled alarm for task $tag in ${delayMillis}ms")
        }.onFailure {
            Logger.error("Could not schedule alarm for task $tag", it)
        }
    }

    fun cancel(context: Context, tag: String) {
        runCatching {
            // Extras play no part in matching, so an intent carrying only the identity of
            // the task is enough to find the alarm scheduled above.
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode(tag),
                taskIntent(context, tag),
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            ) ?: return
            ContextCompat.getSystemService(context, AlarmManager::class.java)?.cancel(pendingIntent)
            pendingIntent.cancel()
            Logger.info("Cancelled alarm for task $tag")
        }.onFailure {
            Logger.error("Could not cancel alarm for task $tag", it)
        }
    }

    private fun taskIntent(context: Context, tag: String) =
        Intent(context, TimedTaskReceiver::class.java).setAction(tag)

    /**
     * PendingIntents are matched on their request code as well as their intent, so tasks
     * with different tags must not share one.
     */
    private fun requestCode(tag: String) = tag.hashCode()
}
