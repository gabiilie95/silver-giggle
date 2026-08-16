package com.ilieinc.dontsleep.timer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ilieinc.core.util.Logger

/**
 * Runs a timed task that was scheduled through [AlarmTimerScheduler] instead of WorkManager.
 * Only ever reached on devices where WorkManager cannot be created — see [TimerManager].
 */
class TimedTaskReceiver : BroadcastReceiver() {
    companion object {
        const val TASK_EXTRA = "TimedTask"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val task = intent.getStringExtra(TASK_EXTRA)
        runCatching {
            when (task) {
                StopServiceWorker::class.java.name -> TimedTasks.stopService(
                    context,
                    intent.getStringExtra(StopServiceWorker.SERVICE_NAME_EXTRA)
                )

                MediaTimeoutWorker::class.java.name -> TimedTasks.pauseMediaPlayback(context)

                else -> Logger.info("Ignoring unknown timed task $task")
            }
        }.onFailure {
            Logger.error("Error running timed task $task", it)
        }
    }
}
