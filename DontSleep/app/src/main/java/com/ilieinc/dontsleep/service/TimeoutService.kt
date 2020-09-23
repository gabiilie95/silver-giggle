package com.ilieinc.dontsleep.service

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.os.PowerManager
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.ilieinc.dontsleep.model.NamedWakeLock
import com.ilieinc.dontsleep.model.ServiceStatusChangedEvent
import com.ilieinc.dontsleep.util.Logger
import com.ilieinc.dontsleep.util.NotificationManager
import com.ilieinc.dontsleep.util.StateHelper
import com.ilieinc.kotlinevents.Event

class TimeoutService : BaseService(
    TimeoutService::class.java,
    TIMEOUT_TAG,
    1
) {
    companion object {
        val serviceStatusChanged = Event(ServiceStatusChangedEvent::class.java)
        const val TIMEOUT_TAG = "DontSleep::TimeoutTag"
        const val TIMEOUT_WAKELOCK_TAG = "DontSleep::TimeoutServiceStopTag"
        private val wakeLock = NamedWakeLock()
        fun isRunning(context: Context) =
            StateHelper.isServiceRunning(context, TimeoutService::class.java)
    }

    private var overlay: View? = null
    override val serviceStatusChanged: Event<ServiceStatusChangedEvent> =
        SleepService.serviceStatusChanged

    override fun initFields() {
        notification = NotificationManager.createScreenTimeoutNotification(this)
    }

    override fun onCreate() {
        super.onCreate()
        if (StateHelper.deviceRequiresOverlay()) {
            showOverlay()
        } else {
            acquireWakeLock()
        }
    }

    override fun onDestroy() {
        if (StateHelper.deviceRequiresOverlay()) {
            removeOverlay(getSystemService(Context.WINDOW_SERVICE) as WindowManager)
        } else {
            wakeLock.release()
        }
        super.onDestroy()
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
        Logger.info("Inserting overlay")
        windowManager.addView(overlay, params)
    }

    private fun acquireWakeLock() {
        val currentWakeLock =
            (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                newWakeLock(
                    PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ON_AFTER_RELEASE,
                    TIMEOUT_WAKELOCK_TAG
                )
            }
        currentWakeLock.acquire(timeout)
        wakeLock.name = TIMEOUT_WAKELOCK_TAG
        wakeLock.lock = currentWakeLock
    }

    private fun removeOverlay(windowManager: WindowManager) {
        if (overlay != null) {
            windowManager.removeView(overlay)
        }
        overlay = null
    }
}