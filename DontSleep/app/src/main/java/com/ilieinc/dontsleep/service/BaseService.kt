package com.ilieinc.dontsleep.service

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import com.ilieinc.dontsleep.manager.BaseServiceManager
import com.ilieinc.dontsleep.util.DontSleepNotificationManager
import kotlinx.coroutines.runBlocking

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

        // Call startForeground IMMEDIATELY to avoid
        // ForegroundServiceStartNotAllowedException.
        // Uses default-state notification (state hasn't loaded yet).
        startForegroundCompat()

        // Now load real state from DataStore and set up timeout.
        // Still blocking because subclass onCreate() code (e.g. acquireWakeLock)
        // needs serviceManager.timeout to be ready when it runs next.
        runBlocking { serviceManager.onCreateService() }

        // Update the notification with real state-dependent content.
        val nm = getSystemService(NotificationManager::class.java)
        nm.notify(serviceManager.serviceId, serviceManager.notification)
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

    private fun startForegroundCompat() {
        with(serviceManager) {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && foregroundServiceTypeFlag != null -> {
                    startForeground(serviceId, notification, foregroundServiceTypeFlag!!)
                }

                else -> {
                    startForeground(serviceId, notification)
                }
            }
        }
    }
}