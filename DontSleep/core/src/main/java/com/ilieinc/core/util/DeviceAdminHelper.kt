package com.ilieinc.core.util

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.ilieinc.core.receiver.DeviceAdminReceiver

object DeviceAdminHelper {
    lateinit var componentName: ComponentName
    private lateinit var deviceManager: DevicePolicyManager

    fun init(context: Context) {
        if (!this::componentName.isInitialized) {
            componentName = ComponentName(context, DeviceAdminReceiver::class.java)
            deviceManager = ContextCompat.getSystemService(context, DevicePolicyManager::class.java)!!
            DeviceAdminReceiver.permissionGranted.tryEmit(adminPermissionGranted())
        }
    }

    fun adminPermissionGranted(): Boolean = deviceManager.isAdminActive(componentName)

    fun requestAdminPermission(activity: Activity) {
        with(Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)) {
            putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName)
            putExtra(
                DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "Please allow this permission to allow the application to turn off the screen."
            )
            activity.startActivity(this)
        }
    }

    fun removeActiveAdmin() {
        deviceManager.removeActiveAdmin(componentName)
    }
}