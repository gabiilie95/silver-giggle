package com.ilieinc.dontsleep.service

import android.content.Context
import android.os.PowerManager
import android.os.PowerManager.WakeLock
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

        private val wakeLocks = hashMapOf<String, WakeLock>()

        fun isWakeLockActive(wakeLockName: String): Boolean {
            return wakeLocks[wakeLockName]?.isHeld ?: false
        }

        fun setWakeLockStatus(
            context: Context,
            wakeLockName: String,
            enabled: Boolean,
            turnOffScreen: Boolean
        ) {
            wakeLocks[wakeLockName].let { wakeLock ->
                if (wakeLock == null || enabled != wakeLock.isHeld) {
                    toggleWakeLock(context, wakeLockName, turnOffScreen)
                }
            }
        }

        fun toggleWakeLock(context: Context, wakeLockName: String, turnOffScreen: Boolean) {
            if (wakeLocks[wakeLockName] == null) {
                wakeLocks[wakeLockName] =
                    (context.getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                        newWakeLock(
                            PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ON_AFTER_RELEASE,
                            wakeLockName
                        )
                    }
            }
            wakeLocks[wakeLockName]!!.let { currentWakeLock ->
                if (!currentWakeLock.isHeld) {
                    val timeout = SharedPreferenceManager.getInstance(context)
                        .getLong(wakeLockName, 500000)
                    wakeLocks[wakeLockName]!!.acquire(timeout)
                    if (turnOffScreen) {
                        val lockScreenDateTime = Calendar.getInstance()
                        lockScreenDateTime.add(Calendar.MILLISECOND, timeout.toInt())
                        TimerManager.setTimedTask<LockScreenWorker>(
                            context,
                            lockScreenDateTime.time,
                            wakeLockName
                        )
                    }
                } else {
                    currentWakeLock.release()
                    if (turnOffScreen) {
                        TimerManager.cancelTask(context, wakeLockName)
                    }
//                    wakeLocks.remove(wakeLockName)
                }
            }
            wakeLockStatusChanged.invoke(
                wakeLockName,
                wakeLocks[wakeLockName]!!.isHeld
            )
        }
    }
}