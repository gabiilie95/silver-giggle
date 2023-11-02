package com.ilieinc.dontsleep.ui.component

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
import com.ilieinc.core.viewmodel.PermissionDialogViewModel
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun CardHelpDialog(
    viewModel: CardViewModel
) {
    val activity = LocalContext.current as Activity
    when (viewModel) {
        is WakeLockCardViewModel -> HelpDialog(
            WakeLockHelpDialogViewModel(
                viewModel::onDismissHelpDialog,
                activity.application
            )
        )
        is ScreenTimeoutCardViewModel -> HelpDialog(
            ScreenTimeoutCardHelpDialogViewModel(
                viewModel::onDismissHelpDialog,
                activity.application
            )
        )
        is MediaTimeoutCardViewModel -> HelpDialog(
            MediaTimeoutCardHelpDialogViewModel(
                viewModel::onDismissHelpDialog,
                activity.application
            )
        )
    }
}

@Composable
fun CardPermissionDialog(
    viewModel: CardViewModel
) {
    val activity = LocalContext.current as Activity
    when (viewModel) {
        is WakeLockCardViewModel -> {
            PermissionDialog(
                WakeLockPermissionDialogViewModel(
                    viewModel::onDismissPermissionDialog,
                    activity.application
                )
            )
        }
        is ScreenTimeoutCardViewModel -> {
            PermissionDialog(
                ScreenTimeoutPermissionDialogViewModel(
                    viewModel::onDismissPermissionDialog,
                    activity
                )
            )
        }
    }
}

@Composable
fun HelpDialog(viewModel: HelpDialogViewModel) {
    with(viewModel) {
        AlertDialog(
            onDismissRequest = ::onDismissRequested,
            title = { Text(title) },
            text = { Text(description) },
            confirmButton = { Button(onClick = ::onDismissRequested) { Text("OK") } },
            dismissButton = {
                if (showRevokePermissionButton) {
                    Button(onClick = this::revokePermission) {
                        Text(text = revokeButtonText)
                    }
                }
            })
    }
}

@Composable
fun PermissionDialog(viewModel: PermissionDialogViewModel) {
    with(viewModel) {
        AlertDialog(
            onDismissRequest = ::onDismissRequested,
            title = { Text(title) },
            text = { Text(description) },
            confirmButton = {
                Button(
                    onClick = ::requestPermission,
                    enabled = confirmButtonEnabled
                ) {
                    Text(confirmButtonText)
                }
            },
            dismissButton = {
                Button(onClick = ::onDismissRequested) {
                    Text("No")
                }
            })
    }
}