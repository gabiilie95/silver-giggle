package com.ilieinc.dontsleep.util

import android.app.admin.DevicePolicyManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

object PermissionHelper {

    fun hasDrawOverPermission(context: Context) = Settings.canDrawOverlays(context)

    fun shouldRequestDrawOverPermission(context: Context) =
        StateHelper.deviceRequiresOverlay() && !hasDrawOverPermission(context)

    fun shouldRequestAdminPermission(deviceManager: DevicePolicyManager) =
        !deviceManager.isAdminActive(DeviceAdminHelper.componentName)

    fun requestDrawOverPermission(context: Context) {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:" + context.packageName)
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}