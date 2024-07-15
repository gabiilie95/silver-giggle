package com.ilieinc.dontsleep.service

import android.content.Context
import android.content.pm.ServiceInfo
import android.graphics.PixelFormat
import android.os.Build
import android.os.PowerManager
import android.text.format.DateFormat
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.ilieinc.dontsleep.R
import com.ilieinc.dontsleep.model.NamedWakeLock
import com.ilieinc.core.util.Logger
import com.ilieinc.core.util.StateHelper
import com.ilieinc.dontsleep.manager.MediaTimeoutServiceManager
import com.ilieinc.dontsleep.manager.WakeLockServiceManager
import com.ilieinc.dontsleep.service.MediaTimeoutService.Companion.MEDIA_TIMEOUT_TAG
import com.ilieinc.dontsleep.util.DontSleepNotificationManager
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.*

class WakeLockService : BaseService(
    serviceManager = WakeLockServiceManager(
        serviceClass = WakeLockService::class.java,
        serviceTag = TIMEOUT_TAG,
        serviceId = 1
    )
) {
    companion object {
        const val TIMEOUT_TAG = "DontSleep::WakeLockTag"
        const val TIMEOUT_WAKELOCK_TAG = "DontSleep::WakeLockServiceStopTag"

        private val wakeLock = NamedWakeLock()
        fun isRunning(context: Context) =
            StateHelper.isServiceRunning(context, WakeLockService::class.java)

        val serviceRunning = MutableStateFlow(false)
    }

    override val binder: ServiceBinder = ServiceBinder(this)

    private var overlay: View? = null

    override fun onCreate() {
        super.onCreate()
        serviceRunning.tryEmit(true)
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
        serviceRunning.tryEmit(false)
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
        currentWakeLock.acquire(serviceManager.timeout)
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