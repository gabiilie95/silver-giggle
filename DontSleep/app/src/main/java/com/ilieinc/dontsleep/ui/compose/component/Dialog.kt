package com.ilieinc.dontsleep.ui.compose.component

import android.app.Activity
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import com.ilieinc.dontsleep.viewmodel.*
import com.ilieinc.dontsleep.viewmodel.base.CardViewModel
import com.ilieinc.dontsleep.viewmodel.base.HelpDialogViewModel
import com.ilieinc.dontsleep.viewmodel.base.PermissionDialogViewModel
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun CardHelpDialog(
    showDialog: MutableStateFlow<Boolean>,
    viewModel: CardViewModel
) {
    val activity = LocalContext.current as Activity
    when (viewModel) {
        is WakeLockCardViewModel -> {
            HelpDialog(
                WakeLockHelpDialogViewModel(
                    showDialog,
                    activity.application
                )
            )
        }
        is ScreenTimeoutCardViewModel -> {
            HelpDialog(
                ScreenTimeoutCardHelpDialogViewModel(
                    showDialog,
                    activity.application
                )
            )
        }
    }
}

@Composable
fun CardPermissionDialog(
    showPermissionDialog: MutableStateFlow<Boolean>,
    viewModel: CardViewModel
) {
    val activity = LocalContext.current as Activity
    when (viewModel) {
        is WakeLockCardViewModel -> {
            PermissionDialog(
                WakeLockPermissionDialogViewModel(
                    showPermissionDialog,
                    activity.application
                )
            )
        }
        is ScreenTimeoutCardViewModel -> {
            PermissionDialog(
                ScreenTimeoutPermissionDialogViewModel(
                    showPermissionDialog,
                    activity
                )
            )
        }
    }
}

@Composable
fun HelpDialog(viewModel: HelpDialogViewModel) {
    AlertDialog(onDismissRequest = { viewModel.showDialog.tryEmit(false) },
        title = { Text(viewModel.title.collectAsState().value) },
        text = { Text(viewModel.description.collectAsState().value) },
        confirmButton = { Button(onClick = { viewModel.showDialog.tryEmit(false) }) { Text("OK") } },
        dismissButton = {
            if (viewModel.showRevokePermissionButton.collectAsState().value) {
                Button(onClick = { viewModel.revokePermission() }) {
                    Text(text = viewModel.revokeButtonText.collectAsState().value)
                }
            }
        })
}

@Composable
fun PermissionDialog(viewModel: PermissionDialogViewModel) {
    AlertDialog(
        onDismissRequest = { viewModel.showDialog.tryEmit(false) },
        title = { Text(viewModel.title.collectAsState().value) },
        text = { Text(viewModel.description.collectAsState().value) },
        confirmButton = {
            Button(
                onClick = { viewModel.requestPermission() },
                enabled = viewModel.confirmButtonEnabled.collectAsState().value
            ) {
                Text(viewModel.confirmButtonText.collectAsState().value)
            }
        },
        dismissButton = {
            Button(onClick = { viewModel.showDialog.tryEmit(false) }) {
                Text("No")
            }
        })
}