package com.ilieinc.dontsleep.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import com.ilieinc.dontsleep.manager.BaseServiceManager
import com.ilieinc.dontsleep.util.DontSleepNotificationManager

abstract class BaseService(
    protected val serviceManager: BaseServiceManager
) : Service() {

    class ServiceBinder(private val boundService: Service) : Binder() {
        fun getService() = boundService
    }

    protected abstract val binder: ServiceBinder

    override fun onBind(intent: Intent?): IBinder? = binder

    override fun onCreate() {
        super.onCreate()
        serviceManager.initContext(this)
        serviceManager.onCreateService()
        with(serviceManager) {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && foregroundServiceTypeFlag != null -> {
                    startForeground(
                        serviceId,
                        notification,
                        foregroundServiceTypeFlag!!
                    )
                }

                else -> {
                    startForeground(serviceManager.serviceId, serviceManager.notification)
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.getBooleanExtra(DontSleepNotificationManager.STOP_COMMAND, false) == true) {
            stopSelf()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        serviceManager.onDestroyService()
        super.onDestroy()
    }
}