@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.ilieinc.dontsleep.ui.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
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
import com.ilieinc.dontsleep.viewmodel.base.HelpDialogViewModel

@Composable
fun CardHelpDialog(
    viewModel: CardViewModel
) {
    val dialogViewModel: HelpDialogViewModel? = when (viewModel) {
        is WakeLockCardViewModel -> hiltViewModel<WakeLockHelpDialogViewModel>()
        is MediaTimeoutCardViewModel -> hiltViewModel<MediaTimeoutCardHelpDialogViewModel>()
        else -> null
    }
    dialogViewModel?.let {
        val state by it.state.collectAsState()
        HelpDialog(
            state = state,
            dialogViewModel = it,
            onDismissRequested = { viewModel.onEvent(OnChangeHelpDialogVisibility(false)) },
            onRevokePermissionClick = it::revokePermission
        )
    }
}

@Composable
fun CardPermissionDialog(
    viewModel: CardViewModel
) {
    val dialogViewModel: WakeLockPermissionDialogViewModel? = when (viewModel) {
        is WakeLockCardViewModel -> hiltViewModel<WakeLockPermissionDialogViewModel>()
        else -> null
    }
    dialogViewModel?.let {
        DialogDismissEventHandler(
            dialogViewModel = it,
            onDismiss = { viewModel.onEvent(OnChangePermissionDialogVisibility(false)) }
        )
        val state by it.state.collectAsState()
        PermissionDialog(
            state,
            onRequestPermission = it::requestPermission,
            onDismissRequested = it::onDismissRequested
        )
    }
}

/** One-off note shown to people who just updated the app. See UpdateNoticeHelper. */
@Composable
fun UpdateNoticeDialog(onDismissRequested: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequested,
        title = { Text(stringResource(R.string.update_notice_title)) },
        text = { Text(stringResource(R.string.update_notice_description)) },
        confirmButton = {
            Button(
                shapes = ButtonDefaults.shapes(),
                onClick = onDismissRequested
            ) {
                Text(stringResource(R.string.ok))
            }
        }
    )
}

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
            }
        )
    }
}
