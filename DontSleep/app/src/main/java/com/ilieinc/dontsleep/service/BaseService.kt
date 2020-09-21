package com.ilieinc.dontsleep.service

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import com.ilieinc.dontsleep.model.ServiceStatusChangedEvent
import com.ilieinc.dontsleep.timer.StopServiceWorker
import com.ilieinc.dontsleep.timer.TimerManager
import com.ilieinc.dontsleep.util.SharedPreferenceManager
import com.ilieinc.dontsleep.util.StateHelper
import com.ilieinc.kotlinevents.Event
import java.util.*

abstract class BaseService(
    private val serviceClass: Class<*>,
    private val serviceTag: String,
    private val id: Int
) : Service() {
    abstract val serviceStatusChanged: Event<ServiceStatusChangedEvent>

    protected lateinit var notification: Notification
    protected abstract val action: (enabled: Boolean) -> Unit
    protected lateinit var timeoutDateTime: Calendar

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        initFields()
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(id, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_NONE)
        } else {
            startForeground(id, notification)
        }
        timeoutDateTime = Calendar.getInstance()
        val timeout = SharedPreferenceManager.getInstance(this)
            .getLong(serviceTag, 500000)
        timeoutDateTime.add(Calendar.MILLISECOND, timeout.toInt())
        serviceStatusChanged.invoke(serviceClass.name, true)
    }

    abstract fun initFields()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            val enabled = it.getBooleanExtra(StateHelper.SERVICE_ENABLED_EXTRA, false)
            executeLogic(enabled)
        }
        return START_STICKY
    }

    private fun executeLogic(enabled: Boolean) {
        action(enabled)
        if (enabled) {
            TimerManager.setTimedTask<StopServiceWorker>(
                this,
                timeoutDateTime.time,
                serviceTag,
                mutableMapOf(StopServiceWorker.SERVICE_NAME_EXTRA to TimeoutService::class.java.name)
            )
        } else {
            TimerManager.cancelTask(this, SleepService.SLEEP_TAG)
            stopSelf()
        }
    }

    override fun onDestroy() {
        serviceStatusChanged.invoke(serviceClass.name, false)
        super.onDestroy()
    }
}