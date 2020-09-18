package com.ilieinc.dontsleep.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.ilieinc.dontsleep.model.NamedWakeLock
import com.ilieinc.dontsleep.model.ServiceStatusChangedEvent
import com.ilieinc.dontsleep.timer.TimerManager
import com.ilieinc.dontsleep.util.NotificationManager
import com.ilieinc.dontsleep.util.SharedPreferenceManager
import com.ilieinc.dontsleep.util.StateHelper
import com.ilieinc.dontsleep.util.StateHelper.SERVICE_ENABLED_EXTRA
import com.ilieinc.kotlinevents.Event

class TimeoutService : Service() {
    companion object {
        val serviceStatusChanged = Event(ServiceStatusChangedEvent::class.java)
        const val TIMEOUT_TAG = "DontSleep::TimeoutTag"

        fun isRunning(context: Context) =
            StateHelper.isServiceRunning(context, TimeoutService::class.java)

        private val wakeLock = NamedWakeLock()

        fun requestDrawOverPermission(context: Context) {
            if (!Settings.canDrawOverlays(context)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + context.packageName)
                )
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            } else {
                val serviceIntent = Intent(context, TimeoutService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent)
                } else {
                    context.startService(serviceIntent)
                }
            }
        }
    }

    private var overlay: View? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            val enabled = it.getBooleanExtra(SERVICE_ENABLED_EXTRA, false)
            setTimeoutStatus(enabled)
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
    }

    private fun setTimeoutStatus(enabled: Boolean) {
        if (enabled) {
            if (StateHelper.deviceRequiresOverlay()) {
                showOverlay()
            } else {
                val currentWakeLock =
                    (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                        newWakeLock(
                            PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ON_AFTER_RELEASE,
                            TIMEOUT_TAG
                        )
                    }
                val timeout = SharedPreferenceManager.getInstance(this)
                    .getLong(TIMEOUT_TAG, 500000)
                currentWakeLock.acquire(timeout)
                wakeLock.name = TIMEOUT_TAG
                wakeLock.lock = currentWakeLock
            }
            serviceStatusChanged.invoke(TimeoutService::class.java.name, true)
        } else {
            if (StateHelper.deviceRequiresOverlay()) {
                removeOverlay(getSystemService(Context.WINDOW_SERVICE) as WindowManager)
            } else {
                wakeLock.release()
                TimerManager.cancelTask(this, TIMEOUT_TAG)
            }
            serviceStatusChanged.invoke(TimeoutService::class.java.name, false)
            stopSelf()
        }
    }

    private fun showOverlay() {
        val windowManager =
            getSystemService(Context.WINDOW_SERVICE) as WindowManager
        removeOverlay(windowManager)
        overlay = View(this)
        val params =
            WindowManager.LayoutParams(
                0,
                0,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                else
                    WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                2098585,
                PixelFormat.TRANSLUCENT
            )
        params.gravity = Gravity.TOP or Gravity.START
        params.x = 0
        params.y = 0
        windowManager.addView(overlay, params)
    }

    private fun removeOverlay(windowManager: WindowManager) {
        if (overlay != null) {
            windowManager.removeView(overlay)
        }
        overlay = null
    }
}