package com.ilieinc.dontsleep.service

import android.app.ForegroundServiceStartNotAllowedException
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.ServiceCompat
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.ilieinc.core.util.Logger
import com.ilieinc.dontsleep.manager.BaseServiceManager
import com.ilieinc.dontsleep.util.DontSleepNotificationManager
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

abstract class BaseService(
    protected val serviceManager: BaseServiceManager
) : Service() {

    class ServiceBinder(private val boundService: Service) : Binder() {
        fun getService() = boundService
    }

    protected abstract val binder: ServiceBinder

    /** Mirrors the service lifetime for the cards and the quick settings tiles. */
    protected abstract val runningState: MutableStateFlow<Boolean>

    /**
     * When the service shuts itself off, as epoch millis, for the quick settings tiles.
     * Null while the persisted state is still loading and when nothing will stop it.
     */
    protected abstract val timeoutState: MutableStateFlow<Long?>

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Logger.error("Error starting ${javaClass.simpleName}", throwable)
        FirebaseCrashlytics.getInstance().recordException(throwable)
    }

    private val serviceScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate + exceptionHandler)

    private var started = false

    override fun onBind(intent: Intent?): IBinder = binder

    final override fun onCreate() {
        super.onCreate()
        serviceManager.initContext(this)

        // Nothing slow may run before this. The platform kills the service if
        // startForeground() is not reached within a few seconds of startForegroundService(),
        // and refuses the call outright when no background-start exemption applies.
        if (!startForegroundSafely()) {
            stopSelf()
            return
        }
        started = true
        runningState.value = true
        onServiceStarted()

        // Reading the persisted state hits the disk, so it stays off the main thread.
        // Everything that depends on it runs in onServiceStateReady().
        serviceScope.launch {
            serviceManager.onCreateService()
            timeoutState.value = serviceManager.timeoutDateTime?.timeInMillis
            onServiceStateReady()
            refreshNotification()
        }
    }

    final override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.getBooleanExtra(DontSleepNotificationManager.STOP_COMMAND, false) == true) {
            stopSelf()
        }
        // Deliberately not sticky: a sticky restart arrives while the app is in the
        // background, where Android 12+ forbids startForeground(). It could only crash.
        return START_NOT_STICKY
    }

    final override fun onDestroy() {
        serviceScope.cancel()
        if (started) {
            started = false
            runCatching { onServiceStopping() }
                .onFailure { Logger.error("Error stopping ${javaClass.simpleName}", it) }
            serviceManager.onDestroyService()
            runningState.value = false
            timeoutState.value = null
        }
        super.onDestroy()
    }

    /** Runs once the service is in the foreground, before the persisted state is available. */
    protected open fun onServiceStarted() = Unit

    /** Runs once the manager has loaded the persisted state. */
    protected open suspend fun onServiceStateReady() = Unit

    /** Runs on teardown. A failure here never prevents the rest of the teardown. */
    protected open fun onServiceStopping() = Unit

    private fun startForegroundSafely(): Boolean = runCatching {
        ServiceCompat.startForeground(
            this,
            serviceManager.serviceId,
            serviceManager.buildNotification(),
            serviceManager.foregroundServiceType
        )
        true
    }.getOrElse { throwable ->
        if (isForegroundStartBlocked(throwable)) {
            // Expected whenever the start had no exemption, e.g. a tile tap on a dozing
            // device. Worth a log line, but it is not a crash to report.
            Logger.error("Foreground start not allowed for ${javaClass.simpleName}", throwable)
        } else {
            Logger.error("Could not start ${javaClass.simpleName} in foreground", throwable)
            FirebaseCrashlytics.getInstance().recordException(throwable)
        }
        false
    }

    private fun isForegroundStartBlocked(throwable: Throwable) =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                throwable is ForegroundServiceStartNotAllowedException

    /**
     * Re-posts through startForeground() rather than NotificationManager.notify(). Both
     * update the notification, but notify() is dropped outright when POST_NOTIFICATIONS is
     * denied — which is exactly when the foreground notification is still on screen showing
     * whatever was posted first. Re-posting an already-foregrounded service is not a
     * foreground start, so no start restriction applies to it.
     */
    private fun refreshNotification() = runCatching {
        ServiceCompat.startForeground(
            this,
            serviceManager.serviceId,
            serviceManager.buildNotification(),
            serviceManager.foregroundServiceType
        )
    }.onFailure {
        Logger.error("Could not refresh the ${javaClass.simpleName} notification", it)
    }
}
