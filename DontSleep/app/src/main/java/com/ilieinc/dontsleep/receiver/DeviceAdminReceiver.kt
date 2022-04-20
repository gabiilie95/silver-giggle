package com.ilieinc.dontsleep.receiver

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.flow.MutableStateFlow

class DeviceAdminReceiver : DeviceAdminReceiver() {
    companion object {
        var permissionGranted = MutableStateFlow(false)
    }

    override fun onEnabled(context: Context, intent: Intent) {
        permissionGranted.tryEmit(true)
        super.onEnabled(context, intent)
    }

    override fun onDisabled(context: Context, intent: Intent) {
        permissionGranted.tryEmit(false)
        super.onDisabled(context, intent)
    }
}