package com.ilieinc.dontsleep.ui

import android.os.Build
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.ilieinc.core.ui.components.ApplicationTopAppBar
import com.ilieinc.core.ui.theme.AppTheme
import com.ilieinc.core.util.StateHelper.needToShowReviewSnackbar
import com.ilieinc.core.util.StateHelper.showReviewSnackbar
import com.ilieinc.dontsleep.MainActivity
import com.ilieinc.dontsleep.ui.component.ActionCard
import com.ilieinc.dontsleep.ui.component.CardHelpDialog
import com.ilieinc.dontsleep.ui.component.CardPermissionDialog
import com.ilieinc.dontsleep.ui.component.RequestNotificationButton
import com.ilieinc.dontsleep.viewmodel.MediaTimeoutCardViewModel
import com.ilieinc.dontsleep.viewmodel.NotificationButtonDialogViewModel
import com.ilieinc.dontsleep.viewmodel.WakeLockCardViewModel
import com.ilieinc.dontsleep.viewmodel.base.CardViewModel

@Composable
fun MainScreen(
    hasNotificationPermission: Boolean,
    notificationPermissionResult: ManagedActivityResultLauncher<String, Boolean>
) {
    val activity = LocalActivity.current
    val snackBarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { ApplicationTopAppBar() },
        snackbarHost = { SnackbarHost(snackBarHostState) },
        bottomBar = {
            Button(
                modifier = Modifier
                    .padding(5.dp)
                    .navigationBarsPadding()
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
                WakeLockTimerCard(
                    modifier = Modifier
                        .padding(5.dp)
                        .animateContentSize()
                )
                MediaTimeoutTimerCard(
                    modifier = Modifier
                        .padding(5.dp)
                        .animateContentSize()
                )
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && activity is MainActivity
                    && !hasNotificationPermission
                ) {
                    AndroidTNotificationPermissionButton(activity, notificationPermissionResult)
                }
            }
        }
        if (activity != null) {
            LaunchedEffect(snackBarHostState) {
                if (needToShowReviewSnackbar(activity.applicationContext)) {
                    showReviewSnackbar(
                        activity.applicationContext,
                        snackBarHostState
                    )
                }
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
            activity,
            notificationPermissionResult
        )
    )
}

@Composable
fun WakeLockTimerCard(
    modifier: Modifier = Modifier,
    viewModel: WakeLockCardViewModel = hiltViewModel()
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    UiCard(
        modifier = modifier,
        viewModel = viewModel
    )
    LaunchedEffect(Unit) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.refreshPermissionState()
        }
    }
}

@Composable
fun MediaTimeoutTimerCard(
    modifier: Modifier = Modifier,
    viewModel: MediaTimeoutCardViewModel = hiltViewModel()
) {
    UiCard(
        modifier = modifier,
        viewModel = viewModel
    )
}

@Composable
private fun UiCard(
    viewModel: CardViewModel,
    modifier: Modifier = Modifier
) {
    with(viewModel) {
        val state by state.collectAsState()
        ActionCard(
            modifier = modifier,
            state = state,
            onEvent = ::onEvent
        )
        if (state.showHelpDialog) {
            CardHelpDialog(viewModel = this)
        }
        if (state.showPermissionDialog) {
            CardPermissionDialog(viewModel = this)
        }
    }
}

@Preview
@Composable
private fun MainScreenPreview() {
    AppTheme {
        MainScreen(
            false, rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = {}
            ))
    }
}