package com.ilieinc.dontsleep.util

import android.app.Activity
import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.ilieinc.dontsleep.R
import java.util.*


object StateHelper {
    enum class TileStates {
        On,
        Off,
        Disabled,
    }

    private const val APP_START_COUNT = "AppStartCount"
    private const val RATING_SHOWN = "RatingShown"

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

    fun createDialog(
        context: Context,
        title: String,
        message: String,
        positiveButtonMessage: String,
        negativeButtonMessage: String,
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
            .setPositiveButton(positiveButtonMessage, clickListener)
            .setNegativeButton(negativeButtonMessage, clickListener)
        return dialogBuilder
    }

    fun requestRatingIfNeeded(activity: Activity) {
        val sharedPreferences = SharedPreferenceManager.getInstance(activity)
        val ratingWasShown = sharedPreferences.getBoolean(RATING_SHOWN, false)
        if (!ratingWasShown) {
            val startNum = sharedPreferences.getInt(APP_START_COUNT, 0)
            sharedPreferences.edit(true) { putInt(APP_START_COUNT, startNum + 1) }
            if (startNum != 0 && startNum % 5 == 0) {
                val view = activity.findViewById<ConstraintLayout>(R.id.container)
                Snackbar.make(
                    view,
                    "Please rate the app if you are enjoying it :)",
                    Snackbar.LENGTH_LONG
                )
                    .setAction(
                        "RATE APP"
                    ) {
                        activity.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=${activity.packageName}")
                            )
                        )
                        sharedPreferences.edit(true) { putBoolean(RATING_SHOWN, true) }
                    }.show()
            }
        }
    }
}