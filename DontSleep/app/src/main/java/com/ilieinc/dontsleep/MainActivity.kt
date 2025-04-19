package com.ilieinc.dontsleep

import android.os.Build
import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.material.color.DynamicColors
import com.google.android.material.elevation.SurfaceColors
import com.ilieinc.core.data.CoreDataStore.PERMISSION_NOTIFICATION_SHOWN_PREF_KEY
import com.ilieinc.core.data.dataStore
import com.ilieinc.core.data.setValue
import com.ilieinc.core.ui.theme.AppTheme
import com.ilieinc.core.util.DeviceAdminHelper
import com.ilieinc.core.util.PermissionHelper
import com.ilieinc.core.util.StateHelper
import com.ilieinc.dontsleep.ui.MainScreen
import com.ilieinc.dontsleep.viewmodel.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = SurfaceColors.SURFACE_0.getColor(this)
        window.navigationBarColor = SurfaceColors.SURFACE_2.getColor(this)
        setContent {
            val context = LocalContext.current
            val scope = rememberCoroutineScope()
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
                scope.launch {
                    context.dataStore.setValue(PERMISSION_NOTIFICATION_SHOWN_PREF_KEY, true)
                    hasNotificationPermission = it
                }
            }
            LaunchedEffect(Unit) {
                scope.launch {
                    StateHelper.initDynamicColorsEnabledProperty(context)
                    StateHelper.updateRatingCountIfNeeded(context)
                }
            }
            setWindowInsetAppearance(window, useDynamicColors, isSystemInDarkTheme())
            AppTheme(useDynamicColors = useDynamicColors) {
                MainScreen(hasNotificationPermission, notificationPermissionResult)
            }
            SideEffect {
                scope.launch {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                        && !hasNotificationPermission
                        && !PermissionHelper.appRequestedNotificationPermissionOnStartup(context)
                    ) {
                        PermissionHelper.requestNotificationPermission(notificationPermissionResult)
                    }
                }
            }
        }
        DeviceAdminHelper.init(applicationContext)
    }

    override fun onStart() {
        super.onStart()
        viewModel.setActivityRunning(true)
    }

    override fun onStop() {
        super.onStop()
        viewModel.setActivityRunning(false)
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
