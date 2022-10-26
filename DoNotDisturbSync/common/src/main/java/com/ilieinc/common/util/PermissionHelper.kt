package com.ilieinc.common.util

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import com.ilieinc.common.util.SharedPreferenceManager

object PermissionHelper {
    const val PERMISSION_NOTIFICATION_SHOWN = "PermissionNotificationShown"

    fun requestNotificationPolicyAccessPermission(context: Context) {
        val intent = Intent(
            Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS,
//            Uri.parse("package:" + context.packageName)
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun hasNotificationPolicyAccessPermission(context: Context): Boolean =
        with(context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager) {
            isNotificationPolicyAccessGranted
        }

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