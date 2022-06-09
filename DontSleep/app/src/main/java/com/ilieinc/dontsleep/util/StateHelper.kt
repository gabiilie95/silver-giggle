package com.ilieinc.dontsleep.util

import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.*

object StateHelper {
    enum class TileStates {
        On,
        Off,
        Disabled
    }

    const val APP_START_COUNT = "AppStartCount"
    const val RATING_SHOWN = "RatingShown"
    const val SHOULD_USE_DYNAMIC_COLORS = "ShouldUseDynamicColors"

    private val overlayDevices = arrayOf(
        "samsung"
    )
    val useDynamicColors = MutableStateFlow(true)

    fun deviceRequiresOverlay(): Boolean {
        return overlayDevices.contains(Build.MANUFACTURER.lowercase(Locale.getDefault()))
    }

    fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (activity in manager.getRunningServices(Int.MAX_VALUE)) {
            if (activity.service.className == serviceClass.name) {
                return true
            }
        }
        return false
    }

    inline fun <reified T> Context.startForegroundService() where T : Service {
        ContextCompat.startForegroundService(this, Intent(this, T::class.java))
    }

    inline fun <reified T> Context.stopService() where T : Service {
        stopService(Intent(this, T::class.java))
    }

    fun requestRatingIfNeeded(context: Context) {
        with(SharedPreferenceManager.getInstance(context)) {
            val ratingWasShown = getBoolean(RATING_SHOWN, false)
            if (!ratingWasShown) {
                val startNum = getInt(APP_START_COUNT, 0)
                edit(true) { putInt(APP_START_COUNT, startNum + 1) }
            }
        }
    }

    fun initDynamicColorsEnabledProperty(context: Context) {
        with(SharedPreferenceManager.getInstance(context)) {
            useDynamicColors.tryEmit(getBoolean(SHOULD_USE_DYNAMIC_COLORS, true))
        }
    }

    fun setDynamicColorsEnabled(context: Context, enabled: Boolean) {
        with(SharedPreferenceManager.getInstance(context)) {
            edit(true){ putBoolean(SHOULD_USE_DYNAMIC_COLORS, enabled) }
            useDynamicColors.tryEmit(enabled)
        }
    }
}