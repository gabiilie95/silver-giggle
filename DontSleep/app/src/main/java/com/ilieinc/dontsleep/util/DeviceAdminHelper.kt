package com.ilieinc.dontsleep.util

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ilieinc.dontsleep.receiver.DeviceAdminReceiver

object DeviceAdminHelper {
    const val REQUEST_CODE = 16565

    lateinit var componentName: ComponentName

    fun init(context: Context) {
        if (!this::componentName.isInitialized) {
            componentName = ComponentName(context, DeviceAdminReceiver::class.java)
        }
    }

    fun getInfoDialog(
        activity: Activity,
        onChangeCallback: (() -> Unit)?
    ): MaterialAlertDialogBuilder {
        val deviceManager =
            ContextCompat.getSystemService(
                activity.applicationContext,
                DevicePolicyManager::class.java
            )!!
        val dialogBuilder = MaterialAlertDialogBuilder(activity)
        val clickListener = DialogInterface.OnClickListener { dialog, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    dialog.dismiss()
                }
                DialogInterface.BUTTON_NEGATIVE -> {
                    onChangeCallback?.invoke()
                }
            }
        }
        var message =
            "When this feature is enabled you will not be able to uninstall the application before first revoking the administrator permission.\n"
        dialogBuilder.setTitle("Sleep Timer Help").setPositiveButton("Ok", clickListener)
        val adminActive = deviceManager.isAdminActive(componentName)
        if (adminActive) {
            message += "You can revoke the permission from this dialog in order to uninstall the application."
            dialogBuilder.setNegativeButton("Revoke Admin Permission", clickListener)
        }
        dialogBuilder.setMessage(message)
        return dialogBuilder
    }
}