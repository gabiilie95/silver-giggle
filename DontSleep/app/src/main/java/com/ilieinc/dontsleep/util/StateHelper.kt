package com.ilieinc.dontsleep.util

import android.app.Activity
import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import com.google.android.material.snackbar.Snackbar
import com.ilieinc.dontsleep.R
import java.util.*

object StateHelper {
    enum class TileStates {
        On,
        Off,
        Disabled
    }

    const val APP_START_COUNT = "AppStartCount"
    const val RATING_SHOWN = "RatingShown"

    private val overlayDevices = arrayOf(
        "samsung"
    )

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
}