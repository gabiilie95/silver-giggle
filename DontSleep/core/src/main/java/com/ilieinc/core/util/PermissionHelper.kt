package com.ilieinc.core.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat

object PermissionHelper {
    const val PERMISSION_NOTIFICATION_SHOWN = "PermissionNotificationShown"

    fun hasDrawOverPermission(context: Context) = Settings.canDrawOverlays(context)

    fun shouldRequestDrawOverPermission(context: Context) =
        StateHelper.deviceRequiresOverlay() && !hasDrawOverPermission(context)

    fun requestDrawOverPermission(context: Context) {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:" + context.packageName)
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun hasNotificationPermission(context: Context): Boolean =
        NotificationManagerCompat.from(context).areNotificationsEnabled()

    fun appRequestedNotificationPermissionOnStartup(context: Context): Boolean =
        SharedPreferenceManager.getInstance(context)
            .getBoolean(PERMISSION_NOTIFICATION_SHOWN, false)

    @RequiresApi(33)
    fun requestNotificationPermission(notificationPermissionRequest: ActivityResultLauncher<String>) {
        notificationPermissionRequest.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    @RequiresApi(33)
    fun requestNotificationPermission(notificationPermissionRequest: ManagedActivityResultLauncher<String, Boolean>) {
        notificationPermissionRequest.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}