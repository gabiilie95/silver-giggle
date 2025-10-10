package com.ilieinc.dontsleep.ui.component

import androidx.activity.compose.LocalActivity
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import com.ilieinc.core.compose.DialogDismissEventHandler
import com.ilieinc.core.ui.model.PermissionDialogUiModel
import com.ilieinc.core.viewmodel.base.DialogViewModel
import com.ilieinc.dontsleep.R
import com.ilieinc.dontsleep.ui.model.CardUiEvent.OnChangeHelpDialogVisibility
import com.ilieinc.dontsleep.ui.model.CardUiEvent.OnChangePermissionDialogVisibility
import com.ilieinc.dontsleep.ui.model.HelpDialogUiState
import com.ilieinc.dontsleep.viewmodel.MediaTimeoutCardHelpDialogViewModel
import com.ilieinc.dontsleep.viewmodel.MediaTimeoutCardViewModel
import com.ilieinc.dontsleep.viewmodel.WakeLockCardViewModel
import com.ilieinc.dontsleep.viewmodel.WakeLockHelpDialogViewModel
import com.ilieinc.dontsleep.viewmodel.WakeLockPermissionDialogViewModel
import com.ilieinc.dontsleep.viewmodel.base.CardViewModel

@Composable
fun CardHelpDialog(
    viewModel: CardViewModel
) {
    val activity = LocalActivity.current ?: return
    when (viewModel) {
        is WakeLockCardViewModel -> WakeLockHelpDialogViewModel(activity.application)
        is MediaTimeoutCardViewModel -> MediaTimeoutCardHelpDialogViewModel(activity.application)
        else -> null
    }?.let { dialogViewModel ->
        val state by dialogViewModel.state.collectAsState()
        HelpDialog(
            state = state,
            dialogViewModel = dialogViewModel,
            onDismissRequested = { viewModel.onEvent(OnChangeHelpDialogVisibility(false)) },
            onRevokePermissionClick = dialogViewModel::revokePermission
        )
    }
}

@Composable
fun CardPermissionDialog(
    viewModel: CardViewModel
) {
    val activity = LocalActivity.current ?: return
    when (viewModel) {
        is WakeLockCardViewModel -> WakeLockPermissionDialogViewModel(activity.application)
        else -> null
    }?.let { dialogViewModel ->
        DialogDismissEventHandler(
            dialogViewModel = dialogViewModel,
            onDismiss = { viewModel.onEvent(OnChangePermissionDialogVisibility(false)) }
        )
        val state by dialogViewModel.state.collectAsState()
        PermissionDialog(
            state,
            onRequestPermission = dialogViewModel::requestPermission,
            onDismissRequested = dialogViewModel::onDismissRequested
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HelpDialog(
    state: HelpDialogUiState,
    dialogViewModel: DialogViewModel,
    onDismissRequested: () -> Unit,
    onRevokePermissionClick: () -> Unit
) {
    DialogDismissEventHandler(
        dialogViewModel = dialogViewModel,
        onDismiss = onDismissRequested
    )
    with(state) {
        AlertDialog(
            onDismissRequest = dialogViewModel::onDismissRequested,
            title = { Text(title) },
            text = { Text(description) },
            confirmButton = {
                Button(
                    shapes = ButtonDefaults.shapes(),
                    onClick = dialogViewModel::onDismissRequested
                ) {
                    Text(stringResource(R.string.ok))
                }
            },
            dismissButton = {
                if (showRevokePermissionButton) {
                    Button(
                        shapes = ButtonDefaults.shapes(),
                        onClick = onRevokePermissionClick
                    ) {
                        Text(text = revokeButtonText)
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
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
                    shapes = ButtonDefaults.shapes(),
                    enabled = confirmButtonEnabled
                ) {
                    Text(confirmButtonText)
                }
            },
            dismissButton = {
                Button(
                    shapes = ButtonDefaults.shapes(),
                    onClick = onDismissRequested
                ) {
                    Text(stringResource(R.string.no))
                }
            }
        )
    }
}
