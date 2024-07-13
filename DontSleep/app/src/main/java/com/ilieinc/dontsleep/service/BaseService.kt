package com.ilieinc.dontsleep.service

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import com.ilieinc.core.util.Logger
import com.ilieinc.core.util.NotificationManager
import com.ilieinc.core.util.SharedPreferenceManager
import com.ilieinc.dontsleep.timer.StopServiceWorker
import com.ilieinc.dontsleep.timer.TimerManager
import com.ilieinc.dontsleep.util.DontSleepNotificationManager
import java.util.*

abstract class BaseService(
    private val serviceClass: Class<*>,
    private val serviceTag: String,
    private val id: Int,
    private val foregroundServiceTypeFlag: Int?
) : Service() {

    class ServiceBinder(private val boundService: Service) : Binder() {
        fun getService() = boundService
    }

    protected abstract val binder: ServiceBinder

    protected lateinit var notification: Notification
    protected var timeoutEnabled: Boolean = true
    protected lateinit var timeoutDateTime: Calendar
    protected var timeout: Long = 500000

    override fun onBind(intent: Intent?): IBinder? = binder

    override fun onCreate() {
        Logger.info("Starting service $serviceClass")
        initFields()
        super.onCreate()
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && foregroundServiceTypeFlag != null -> {
                startForeground(id, notification, foregroundServiceTypeFlag)
            }
            else -> {
                startForeground(id, notification)
            }
        }
        timeout = if (timeoutEnabled) {
            SharedPreferenceManager.getInstance(this)
                .getLong(serviceTag, 500000L)
        } else {
            Int.MAX_VALUE.toLong()
        }
        timeoutDateTime = Calendar.getInstance()
        timeoutDateTime.add(Calendar.MILLISECOND, timeout.toInt())
        if (timeoutEnabled) {
            TimerManager.setTimedTask<StopServiceWorker>(
                this,
                timeoutDateTime.time,
                serviceTag,
                mutableMapOf(StopServiceWorker.SERVICE_NAME_EXTRA to serviceClass.name)
            )
        }
    }

    abstract fun initFields()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.getBooleanExtra(DontSleepNotificationManager.STOP_COMMAND, false) == true) {
            stopSelf()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        TimerManager.cancelTask(this, serviceTag)
        super.onDestroy()
    }

    companion object {
        const val TIMEOUT_ENABLED_EXTRA = "TimeoutEnabled"
    }
}