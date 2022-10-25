@file:OptIn(ExperimentalMaterial3Api::class)

package com.ilieinc.dontsleep.ui.compose

import android.app.Activity
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ilieinc.dontsleep.MainActivity
import com.ilieinc.dontsleep.receiver.DeviceAdminReceiver
import com.ilieinc.dontsleep.ui.compose.component.*
import com.ilieinc.dontsleep.ui.theme.AppTheme
import com.ilieinc.dontsleep.util.StateHelper.needToShowReviewSnackbar
import com.ilieinc.dontsleep.util.StateHelper.showReviewSnackbar
import com.ilieinc.dontsleep.viewmodel.NotificationButtonDialogViewModel
import com.ilieinc.dontsleep.viewmodel.WakeLockCardViewModel
import com.ilieinc.dontsleep.viewmodel.ScreenTimeoutCardViewModel
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun MainScreen(
    hasNotificationPermission: Boolean,
    notificationPermissionResult: ManagedActivityResultLauncher<String, Boolean>
) {
    val activity = (LocalContext.current as? Activity)
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { DontSleepTopAppBar() },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Button(
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth(),
                onClick = { activity?.finish() }) {
                Text("Close")
            }
        }
    ) { paddingValues ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                WakeLockTimerCard()
                ScreenTimeoutTimerCard()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && activity is MainActivity
                    && !hasNotificationPermission
                ) {
                    AndroidTNotificationPermissionButton(activity, notificationPermissionResult)
                }
            }
        }
        if (activity != null && needToShowReviewSnackbar(activity.applicationContext)) {
            LaunchedEffect(snackbarHostState) {
                showReviewSnackbar(
                    activity.applicationContext,
                    snackbarHostState
                )
            }
        }
    }
}

@RequiresApi(33)
@Composable
fun AndroidTNotificationPermissionButton(
    activity: MainActivity,
    notificationPermissionResult: ManagedActivityResultLauncher<String, Boolean>
) {
    RequestNotificationButton(
        NotificationButtonDialogViewModel(
            MutableStateFlow(false),
            activity,
            notificationPermissionResult
        )
    )
}

@Composable
fun WakeLockTimerCard(viewModel: WakeLockCardViewModel = viewModel()) {
    val model = remember { viewModel }
    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> model.refreshPermissionState()
            else -> {}
        }
    }
    ActionCard(model)
}

@Composable
fun ScreenTimeoutTimerCard(viewModel: ScreenTimeoutCardViewModel = viewModel()) {
    val model = remember { viewModel }
    val permissionGranted by DeviceAdminReceiver.permissionGranted.collectAsState()
    model.permissionRequired.tryEmit(!permissionGranted)
    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> model.refreshPermissionState()
            else -> {}
        }
    }
    ActionCard(model)
}

@Preview
@Composable
fun MainScreenPreview() {
    AppTheme {
        MainScreen(false, rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = {}
        ))
    }
}