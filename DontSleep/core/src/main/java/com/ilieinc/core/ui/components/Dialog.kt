package com.ilieinc.core.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.ilieinc.core.R
import com.ilieinc.core.compose.DialogDismissEventHandler
import com.ilieinc.core.viewmodel.PermissionDialogViewModel
import com.ilieinc.core.viewmodel.RatingDialogViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RatingDialog(viewModel: RatingDialogViewModel, onDismiss: () -> Unit) {
    DialogDismissEventHandler(
        dialogViewModel = viewModel,
        onDismiss = onDismiss
    )
    with(viewModel) {
        AlertDialog(onDismissRequest = this::onDismissRequested,
            title = { Text(text = stringResource(R.string.rate_title)) },
            text = {
                Text(
                    text = stringResource(R.string.rate_description)
                )
            },
            confirmButton = {
                Button(
                    shapes = ButtonDefaults.shapes(),
                    onClick = ::requestReview
                ) {
                    Text(stringResource(R.string.rate_app))
                }
            },
            dismissButton = {
                Button(
                    shapes = ButtonDefaults.shapes(),
                    onClick = ::onDismissRequested
                ) {
                    Text(text = stringResource(R.string.dismiss))
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NotificationInfoDialog(viewModel: PermissionDialogViewModel) {
    with(viewModel) {
        AlertDialog(
            onDismissRequest = ::onDismissRequested,
            confirmButton = {
                Button(
                    shapes = ButtonDefaults.shapes(),
                    onClick = ::onDismissRequested
                ) {
                    Text(text = stringResource(R.string.dismiss))
                }
            },
            title = { Text(text = stringResource(R.string.notifications_info)) },
            text = { Text(text = stringResource(R.string.notification_info_description)) })
    }
}