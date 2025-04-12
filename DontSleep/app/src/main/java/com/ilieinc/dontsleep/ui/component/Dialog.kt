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
import com.ilieinc.dontsleep.R
import com.ilieinc.dontsleep.ui.model.CardUiEvent
import com.ilieinc.dontsleep.ui.model.HelpDialogUiState

@Composable
fun CardHelpDialog(
    viewModel: CardViewModel
) {
    val activity = LocalContext.current as Activity
    val dialogViewModel = when (viewModel) {
        is WakeLockCardViewModel -> WakeLockHelpDialogViewModel(
            {viewModel.onEvent(CardUiEvent.OnChangeHelpDialogVisibility(false))},
            activity.application
        )

        is MediaTimeoutCardViewModel -> MediaTimeoutCardHelpDialogViewModel(
            {viewModel.onEvent(CardUiEvent.OnChangeHelpDialogVisibility(false))},
            activity.application
        )

        else -> null
    }
    dialogViewModel?.let {
        val state by it.state.collectAsState()
        HelpDialog(
            state,
            onDismissRequested = it::onDismissRequested,
            onRevokePermissionClick = it::revokePermission
        )
    }
}

@Composable
fun CardPermissionDialog(
    viewModel: CardViewModel
) {
    val activity = LocalContext.current as Activity
    val dialogViewModel = when (viewModel) {
        is WakeLockCardViewModel -> {
            WakeLockPermissionDialogViewModel(
                application = activity.application,
                onDismissRequestedCallback = {
                    viewModel.onEvent(CardUiEvent.OnChangePermissionDialogVisibility(false))
                }
            )
        }

        else -> null
    }
    dialogViewModel?.let {
        val state by it.state.collectAsState()
        PermissionDialog(
            state,
            onRequestPermission = it::requestPermission,
            onDismissRequested = it::onDismissRequested
        )
    }
}

@Composable
fun HelpDialog(
    uiModel: HelpDialogUiState,
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