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
import com.ilieinc.core.data.CoreDataStore.PERMISSION_NOTIFICATION_SHOWN_PREF_KEY
import com.ilieinc.core.data.dataStore
import com.ilieinc.core.data.getValue
import androidx.core.net.toUri

object PermissionHelper {
    fun hasDrawOverPermission(context: Context) = Settings.canDrawOverlays(context)

    fun shouldRequestDrawOverPermission(context: Context) =
        StateHelper.deviceRequiresOverlay() && !hasDrawOverPermission(context)

    fun requestDrawOverPermission(context: Context) {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            ("package:" + context.packageName).toUri()
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun hasNotificationPermission(context: Context): Boolean =
        NotificationManagerCompat.from(context).areNotificationsEnabled()

    suspend fun appRequestedNotificationPermissionOnStartup(context: Context): Boolean =
        context.dataStore.getValue(PERMISSION_NOTIFICATION_SHOWN_PREF_KEY, false)

    @RequiresApi(33)
    fun requestNotificationPermission(notificationPermissionRequest: ActivityResultLauncher<String>) {
        notificationPermissionRequest.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    @RequiresApi(33)
    fun requestNotificationPermission(notificationPermissionRequest: ManagedActivityResultLauncher<String, Boolean>) {
        notificationPermissionRequest.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}