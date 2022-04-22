@file:OptIn(ExperimentalMaterial3Api::class)

package com.ilieinc.dontsleep.ui.compose

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ilieinc.dontsleep.receiver.DeviceAdminReceiver
import com.ilieinc.dontsleep.ui.compose.component.ActionCard
import com.ilieinc.dontsleep.ui.compose.component.DontSleepTopAppBar
import com.ilieinc.dontsleep.ui.compose.component.OnLifecycleEvent
import com.ilieinc.dontsleep.ui.theme.AppTheme
import com.ilieinc.dontsleep.viewmodel.WakeLockCardViewModel
import com.ilieinc.dontsleep.viewmodel.ScreenTimeoutCardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val activity = (LocalContext.current as? Activity)
//    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { DontSleepTopAppBar() },
//        snackbarHost = {
//            SnackbarHost(snackbarHostState)
//        },
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
            }
        }
    }
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
        MainScreen()
    }
}