package com.ilieinc.dontsleep.util

import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ilieinc.dontsleep.R
import java.util.*


object StateHelper {
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

    inline fun <reified T> Context.startForegroundService() where T : Service {
        ContextCompat.startForegroundService(this, Intent(this, T::class.java))
    }

    inline fun <reified T> Context.stopService() where T : Service {
        stopService(Intent(this, T::class.java))
    }

    fun createDialog(
        context: Context,
        title: String,
        message: String,
        successCallback: (() -> Unit)?
    ): MaterialAlertDialogBuilder {
        val dialogBuilder = MaterialAlertDialogBuilder(context)
        val clickListener = DialogInterface.OnClickListener { dialog, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    successCallback?.invoke()
                }
                else -> {
                    dialog.dismiss()
                }
            }
        }
        dialogBuilder.setTitle(title)
            .setMessage(message)
            .setPositiveButton(context.getString(R.string.yes), clickListener)
            .setNegativeButton(context.getString(R.string.no), clickListener)
        return dialogBuilder
    }

    fun getTimeoutInfoDialog(
        context: Context,
        onChangeCallback: (() -> Unit)?
    ): MaterialAlertDialogBuilder {
        val dialogBuilder = MaterialAlertDialogBuilder(context)
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
            "This feature enables you to turn off your screen timeout with the click of a button.\n"
        dialogBuilder.setTitle("Don't Sleep! Help").setPositiveButton("Ok", clickListener)
        if (PermissionHelper.hasDrawOverPermission(context)) {
            message += "You can revoke the draw over permission from this dialog."
            dialogBuilder.setNegativeButton("Revoke Draw Over Permission", clickListener)
        } else if (PermissionHelper.shouldRequestDrawOverPermission(context)) {
            message += "In order to use this feature you need to allow draw over permissions."
        }
        dialogBuilder.setMessage(message)
        return dialogBuilder
    }
}