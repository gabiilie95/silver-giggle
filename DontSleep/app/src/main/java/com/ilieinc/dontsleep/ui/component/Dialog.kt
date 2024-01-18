package com.ilieinc.dontsleep.ui.component

import android.app.Activity
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.ilieinc.core.ui.model.PermissionDialogUiModel
import com.ilieinc.dontsleep.viewmodel.*
import com.ilieinc.dontsleep.viewmodel.base.CardViewModel
import com.ilieinc.core.viewmodel.PermissionDialogViewModel
import com.ilieinc.dontsleep.R
import com.ilieinc.dontsleep.ui.model.HelpDialogUiModel

@Composable
fun CardHelpDialog(
    viewModel: CardViewModel,
    onDismissHelpDialog: () -> Unit
) {
    val activity = LocalContext.current as Activity
    val dialogViewModel = when (viewModel) {
        is WakeLockCardViewModel -> WakeLockHelpDialogViewModel(
            onDismissHelpDialog,
            activity.application
        )

        is MediaTimeoutCardViewModel -> MediaTimeoutCardHelpDialogViewModel(
            onDismissHelpDialog,
            activity.application
        )

        else -> null
    }
    dialogViewModel?.let {
        val uiModel by it.uiModel.collectAsState()
        HelpDialog(
            uiModel,
            onDismissRequested = onDismissHelpDialog,
            onRevokePermissionClick = it::revokePermission
        )
    }
}

@Composable
fun CardPermissionDialog(
    viewModel: CardViewModel,
    onDismissRequested: () -> Unit
) {
    val activity = LocalContext.current as Activity
    val dialogViewModel = when (viewModel) {
        is WakeLockCardViewModel -> {
            WakeLockPermissionDialogViewModel(
                onDismissRequested,
                activity.application
            )
        }

        else -> null
    }
    dialogViewModel?.let {
        val uiModel by it.uiModel.collectAsState()
        PermissionDialog(
            uiModel,
            onRequestPermission = it::requestPermission,
            onDismissRequested = onDismissRequested
        )
    }
}

@Composable
fun HelpDialog(
    uiModel: HelpDialogUiModel,
    onDismissRequested: () -> Unit,
    onRevokePermissionClick: () -> Unit
) {
    with(uiModel) {
        AlertDialog(
            onDismissRequest = onDismissRequested,
            title = { Text(title) },
            text = { Text(description) },
            confirmButton = {
                Button(onClick = onDismissRequested) {
                    Text(stringResource(R.string.ok))
                }
            },
            dismissButton = {
                if (showRevokePermissionButton) {
                    Button(onClick = onRevokePermissionClick) {
                        Text(text = revokeButtonText)
                    }
                }
            })
    }
}

@Composable
fun PermissionDialog(
    uiModel: PermissionDialogUiModel,
    onRequestPermission: () -> Unit,
    onDismissRequested: () -> Unit
) {
    with(uiModel) {
        AlertDialog(
            onDismissRequest = onDismissRequested,
            title = { Text(title) },
            text = { Text(description) },
            confirmButton = {
                Button(
                    onClick = onRequestPermission,
                    enabled = confirmButtonEnabled
                ) {
                    Text(confirmButtonText)
                }
            },
            dismissButton = {
                Button(onClick = onDismissRequested) {
                    Text(stringResource(R.string.no))
                }
            })
    }
}