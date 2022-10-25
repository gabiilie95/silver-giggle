@file:OptIn(ExperimentalMaterial3Api::class)

package com.ilieinc.donotdisturbsync.ui.compose

import android.app.Activity
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.ilieinc.donotdisturbsync.MainActivity
import com.ilieinc.donotdisturbsync.ui.compose.component.*
import com.ilieinc.donotdisturbsync.ui.theme.AppTheme
import com.ilieinc.donotdisturbsync.util.StateHelper.needToShowReviewSnackbar
import com.ilieinc.donotdisturbsync.util.StateHelper.showReviewSnackbar

@Composable
fun MainScreen(
    hasNotificationPermission: Boolean,
    accessNotificationPolicyPermissionResult: ManagedActivityResultLauncher<String, Boolean>
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
                DoNotDisturbSyncCard()
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