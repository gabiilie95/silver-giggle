package com.ilieinc.dontsleep

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.edit
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.material.color.DynamicColors
import com.google.android.material.elevation.SurfaceColors
import com.ilieinc.dontsleep.ui.compose.MainScreen
import com.ilieinc.dontsleep.ui.theme.AppTheme
import com.ilieinc.dontsleep.util.DeviceAdminHelper
import com.ilieinc.dontsleep.util.PermissionHelper
import com.ilieinc.dontsleep.util.SharedPreferenceManager
import com.ilieinc.dontsleep.util.StateHelper

class MainActivity : ComponentActivity() {

    private val notificationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        SharedPreferenceManager.getInstance(this).edit(true) {
            putBoolean(PermissionHelper.PERMISSION_NOTIFICATION_SHOWN, true)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StateHelper.initDynamicColorsEnabledProperty(this)
        window.statusBarColor = SurfaceColors.SURFACE_0.getColor(this)
        window.navigationBarColor = SurfaceColors.SURFACE_2.getColor(this)
        setContent {
            val useDynamicColors by StateHelper.useDynamicColors.collectAsState()
            if (useDynamicColors) {
                DynamicColors.applyToActivityIfAvailable(this)
            }
            WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars =
                !isSystemInDarkTheme()
            WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightNavigationBars =
                !isSystemInDarkTheme()
            AppTheme(useDynamicColors = useDynamicColors) {
                MainScreen()
            }
        }
        DeviceAdminHelper.init(applicationContext)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
            && !PermissionHelper.hasNotificationPermission(this)
            && !PermissionHelper.appRequestedNotificationPermissionOnStartup(this)
        ) {
            PermissionHelper.requestNotificationPermission(notificationPermissionRequest)
        }
        StateHelper.updateRatingCountIfNeeded(this)
    }
}
