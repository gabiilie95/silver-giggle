package com.ilieinc.dontsleep.util

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import java.util.*


object StateHelper {
    const val SERVICE_ENABLED_EXTRA = "ServiceEnabled"

    private val overlayDevices = arrayOf(
        "samsung"
    )

    fun deviceRequiresOverlay(): Boolean {
        return true || overlayDevices.contains(Build.MANUFACTURER.toLowerCase(Locale.getDefault()))
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
}