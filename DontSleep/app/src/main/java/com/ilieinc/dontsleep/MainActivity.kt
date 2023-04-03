package com.ilieinc.dontsleep

import android.os.Build
import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.core.content.edit
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.material.color.DynamicColors
import com.google.android.material.elevation.SurfaceColors
import com.ilieinc.dontsleep.ui.compose.MainScreen
import com.ilieinc.core.ui.theme.AppTheme
import com.ilieinc.core.util.DeviceAdminHelper
import com.ilieinc.core.util.PermissionHelper
import com.ilieinc.core.util.SharedPreferenceManager
import com.ilieinc.core.util.StateHelper

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StateHelper.initDynamicColorsEnabledProperty(this)
        window.statusBarColor = SurfaceColors.SURFACE_0.getColor(this)
        window.navigationBarColor = SurfaceColors.SURFACE_2.getColor(this)
        setContent {
            val useDynamicColors by StateHelper.useDynamicColors.collectAsState()
            var hasNotificationPermission by remember {
                mutableStateOf(
                    PermissionHelper.hasNotificationPermission(
                        this
                    )
                )
            }
            val notificationPermissionResult = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) {
                SharedPreferenceManager.getInstance(this).edit(true) {
                    putBoolean(PermissionHelper.PERMISSION_NOTIFICATION_SHOWN, true)
                }
                hasNotificationPermission = it
            }
            setWindowInsetAppearance(window, useDynamicColors, isSystemInDarkTheme())
            AppTheme(useDynamicColors = useDynamicColors) {
                MainScreen(hasNotificationPermission, notificationPermissionResult)
            }
            SideEffect {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                    && !hasNotificationPermission
                    && !PermissionHelper.appRequestedNotificationPermissionOnStartup(this)
                ) {
                    PermissionHelper.requestNotificationPermission(notificationPermissionResult)
                }
            }
        }
        DeviceAdminHelper.init(applicationContext)
        StateHelper.updateRatingCountIfNeeded(this)
    }

    private fun setWindowInsetAppearance(
        window: Window,
        useDynamicColors: Boolean,
        systemInDarkTheme: Boolean
    ) {
        if (useDynamicColors) {
            DynamicColors.applyToActivityIfAvailable(this)
        }
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars =
            !systemInDarkTheme
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightNavigationBars =
            !systemInDarkTheme
    }
}
