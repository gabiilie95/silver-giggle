package com.ilieinc.core.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.ilieinc.core.viewmodel.PermissionDialogViewModel
import com.ilieinc.core.viewmodel.RatingDialogViewModel

@Composable
fun RatingDialog(viewModel: RatingDialogViewModel){
    with(viewModel) {
        AlertDialog(onDismissRequest = this::onDismissRequested,
            title = { Text(text = "Rate this app") },
            text = {
                Text(
                    text = "If you enjoy this app please feel free to rate it on the Google Play " +
                            "Store, it would help me out a lot :)\n" +
                            "For any feedback or suggestions either leave a review, " +
                            "or contact me directly at gabiilie95@gmail.com"
                )
            },
            confirmButton = {
                Button(onClick = {
                    requestReview()
                }) {
                    Text("Rate App")
                }
            },
            dismissButton = {
                Button(onClick = this::onDismissRequested) {
                    Text(text = "Dismiss")
                }
            }
        )
    }
}

@Composable
fun NotificationInfoDialog(viewModel: PermissionDialogViewModel) {
    with(viewModel) {
        AlertDialog(onDismissRequest = this::onDismissRequested,
            confirmButton = {
                Button(onClick = this::onDismissRequested) {
                    Text(text = "Dismiss")
                }
            },
            title = { Text(text = "Notifications Info") },
            text = { Text(text = "This permission is needed if you want to be shown useful notifications when using the application.\n\nNotifications include Time Remaining, and an option to quickly stop the application.") })
    }
}