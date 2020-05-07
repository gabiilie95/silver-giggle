package com.ilieinc.dontsleep.service

import android.content.Context
import android.os.PowerManager
import com.ilieinc.dontsleep.model.WakeLockChangedEvent
import com.ilieinc.dontsleep.timer.LockScreenWorker
import com.ilieinc.dontsleep.timer.TimerManager
import com.ilieinc.dontsleep.util.SharedPreferenceManager
import com.ilieinc.kotlinevents.Event
import java.util.*

class WakeLockManager {
    companion object {
        val wakeLockStatusChanged = Event(WakeLockChangedEvent::class.java)

        const val AWAKE_WAKELOCK_TAG = "DontSleep::AwakeWakeLogTag"
        const val SLEEP_TIMER_WAKELOCK_TAG = "DontSleep::SleepTimerWakeLogTag"

        private var awakeTimerWakeLock: PowerManager.WakeLock? = null
        private var sleepTimerWakeLock: PowerManager.WakeLock? = null

        val awakeTimerActive: Boolean
            get() = awakeTimerWakeLock?.isHeld ?: false
        val sleepTimerActive: Boolean
            get() = sleepTimerWakeLock?.isHeld ?: false

        fun setAwakeWakeLockStatus(context: Context, enabled: Boolean) {
            if (enabled != awakeTimerActive) {
                toggleAwakeWakeLock(context)
            }
        }

        fun setSleepTimerWakeLockStatus(context: Context, enabled: Boolean) {
            if (enabled != sleepTimerActive) {
                toggleSleepTimerWakeLock(context)
            }
        }

        fun toggleAwakeWakeLock(context: Context) {
            awakeTimerWakeLock = if (awakeTimerWakeLock == null) {
                (context.getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                    newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, AWAKE_WAKELOCK_TAG).apply {
                        acquire(
                            SharedPreferenceManager.getInstance(context)
                                .getLong(AWAKE_WAKELOCK_TAG, 500000)
                        )
                    }
                }
            } else {
                awakeTimerWakeLock!!.release()
                null
            }
            wakeLockStatusChanged.invoke(AWAKE_WAKELOCK_TAG, awakeTimerActive)
        }

        fun toggleSleepTimerWakeLock(context: Context) {
            val timeout = SharedPreferenceManager.getInstance(context)
                .getLong(SLEEP_TIMER_WAKELOCK_TAG, 500000)
            sleepTimerWakeLock = if (sleepTimerWakeLock == null) {
                (context.getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                    newWakeLock(
                        PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
                        SLEEP_TIMER_WAKELOCK_TAG
                    ).apply {
                        acquire(timeout)
                        val lockScreenDateTime = Calendar.getInstance()
                        lockScreenDateTime.add(Calendar.MILLISECOND, timeout.toInt())
                        TimerManager.setTimedTask<LockScreenWorker>(
                            context,
                            lockScreenDateTime.time,
                            SLEEP_TIMER_WAKELOCK_TAG
                        )
                    }
                }
            } else {
                sleepTimerWakeLock!!.release()
                TimerManager.cancelTask(context, SLEEP_TIMER_WAKELOCK_TAG)
                null
            }
            wakeLockStatusChanged.invoke(SLEEP_TIMER_WAKELOCK_TAG, sleepTimerActive)
        }
    }
}