package com.ilieinc.dontsleep.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import com.ilieinc.dontsleep.model.ServiceStatusChangedEvent
import com.ilieinc.dontsleep.timer.LockScreenWorker
import com.ilieinc.dontsleep.timer.TimerManager
import com.ilieinc.dontsleep.util.NotificationManager
import com.ilieinc.dontsleep.util.SharedPreferenceManager
import com.ilieinc.dontsleep.util.StateHelper
import com.ilieinc.dontsleep.util.StateHelper.SERVICE_ENABLED_EXTRA
import com.ilieinc.kotlinevents.Event
import java.util.*

class SleepService : Service() {
    companion object {
        val serviceStatusChanged = Event(ServiceStatusChangedEvent::class.java)
        const val SLEEP_TAG = "DontSleep::SleepTag"
        fun isRunning(context: Context) =
            StateHelper.isServiceRunning(context, SleepService::class.java)
    }

    private var timeoutServiceIntent: Intent? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            val enabled = it.getBooleanExtra(SERVICE_ENABLED_EXTRA, false)
            setSleepTimerStatus(enabled)
        }
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        val notification =
            NotificationManager.createScreenTimeoutNotification(applicationContext)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_NONE)
        } else {
            startForeground(2, notification)
        }
        serviceStatusChanged.invoke(SleepService::class.java.name, true)
    }

    private fun setSleepTimerStatus(enabled: Boolean) {
        if (enabled) {
            timeoutServiceIntent = Intent(this, TimeoutService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(timeoutServiceIntent)
            } else {
                startService(timeoutServiceIntent)
            }
            val lockScreenDateTime = Calendar.getInstance()
            val timeout = SharedPreferenceManager.getInstance(this)
                .getLong(SLEEP_TAG, 500000)
            lockScreenDateTime.add(Calendar.MILLISECOND, timeout.toInt())
            TimerManager.setTimedTask<LockScreenWorker>(
                this,
                lockScreenDateTime.time,
                SLEEP_TAG
            )
        } else {
            TimerManager.cancelTask(this, SLEEP_TAG)
            stopService(timeoutServiceIntent)
            stopSelf()
        }
    }

    override fun onDestroy() {
        serviceStatusChanged.invoke(SleepService::class.java.name, false)
        super.onDestroy()
    }
}