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
    private val id: Int
) : Service() {

    class ServiceBinder(private val boundService: Service) : Binder() {
        fun getService() = boundService
    }

    protected abstract val binder: ServiceBinder

    protected lateinit var notification: Notification
    protected lateinit var timeoutDateTime: Calendar
    protected var timeout: Long = 500000

    override fun onBind(intent: Intent?): IBinder? = binder

    override fun onCreate() {
        Logger.info("Starting service $serviceClass")
        initFields()
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(id, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_NONE)
        } else {
            startForeground(id, notification)
        }
        timeout = SharedPreferenceManager.getInstance(this)
            .getLong(serviceTag, 500000)
        timeoutDateTime = Calendar.getInstance()
        timeoutDateTime.add(Calendar.MILLISECOND, timeout.toInt())
        TimerManager.setTimedTask<StopServiceWorker>(
            this,
            timeoutDateTime.time,
            serviceTag,
            mutableMapOf(StopServiceWorker.SERVICE_NAME_EXTRA to serviceClass.name)
        )
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
}