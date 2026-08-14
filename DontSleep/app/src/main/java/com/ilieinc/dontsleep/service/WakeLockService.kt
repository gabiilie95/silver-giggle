package com.ilieinc.dontsleep.service

import android.graphics.PixelFormat
import android.os.Build
import android.os.PowerManager
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.ilieinc.core.util.Logger
import com.ilieinc.core.util.StateHelper
import com.ilieinc.dontsleep.manager.BaseServiceManager
import com.ilieinc.dontsleep.manager.WakeLockServiceManager
import com.ilieinc.dontsleep.model.NamedWakeLock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

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

        private val _serviceRunning = MutableStateFlow(false)
        val serviceRunning = _serviceRunning.asStateFlow()

        fun isRunning() = _serviceRunning.value

        private const val OVERLAY_FLAGS = 2098585
    }

    override val binder: ServiceBinder = ServiceBinder(this)
    override val runningState = _serviceRunning

    private val wakeLock = NamedWakeLock()
    private var overlay: View? = null

    override fun onServiceStarted() {
        // Hold the screen from the moment the service starts rather than waiting for the
        // persisted state, so a slow or failed state read cannot let the screen sleep.
        if (StateHelper.deviceRequiresOverlay()) {
            showOverlay()
        } else {
            acquireWakeLock(BaseServiceManager.DEFAULT_TIMEOUT)
        }
    }

    override fun onServiceStateReady() {
        // Re-arm with the real timeout. The scheduled worker is what normally stops the
        // service; the lock timeout is only a backstop if the process outlives it.
        if (wakeLock.lock != null) {
            acquireWakeLock(serviceManager.timeout)
        }
    }

    override fun onServiceStopping() {
        // Both are no-ops when the matching resource was never taken, so neither branch
        // needs to know which strategy onServiceStarted() picked.
        removeOverlay()
        wakeLock.release()
    }

    private fun showOverlay() {
        removeOverlay()
        val view = View(this)
        val params = WindowManager.LayoutParams(
            0,
            0,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
            OVERLAY_FLAGS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 0
            y = 0
        }
        Logger.info("Inserting overlay")
        runCatching {
            (getSystemService(WINDOW_SERVICE) as WindowManager).addView(view, params)
            overlay = view
        }.onFailure {
            // The overlay permission can be revoked at any time, which surfaces here as a
            // BadTokenException. Fall back to a wake lock instead of silently doing nothing.
            Logger.error("Could not insert overlay, falling back to a wake lock", it)
            acquireWakeLock(BaseServiceManager.DEFAULT_TIMEOUT)
        }
    }

    private fun removeOverlay() {
        val view = overlay ?: return
        overlay = null
        runCatching {
            (getSystemService(WINDOW_SERVICE) as WindowManager).removeView(view)
        }.onFailure {
            Logger.error("Could not remove overlay", it)
        }
    }

    private fun acquireWakeLock(timeout: Long) {
        wakeLock.release()
        runCatching {
            val newLock = (getSystemService(POWER_SERVICE) as PowerManager).newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ON_AFTER_RELEASE,
                TIMEOUT_WAKELOCK_TAG
            )
            newLock.acquire(timeout)
            wakeLock.name = TIMEOUT_WAKELOCK_TAG
            wakeLock.lock = newLock
        }.onFailure {
            Logger.error("Could not acquire wake lock", it)
        }
    }
}
