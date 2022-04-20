package com.ilieinc.dontsleep.ui.compose.component

import android.app.Activity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.StarRate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import com.google.android.play.core.review.ReviewManagerFactory
import com.ilieinc.dontsleep.util.Logger

@Composable
fun DontSleepTopAppBar() {
    val activity = (LocalContext.current as? Activity)
    var showRateDialog by remember { mutableStateOf(false) }

    SmallTopAppBar(title = {
        Text(
            "Don't Sleep!",
            fontWeight = FontWeight.Bold
        )
    },
        actions = {
            IconButton(onClick = { showRateDialog = true }) {
                Icon(Icons.Outlined.StarRate, "Rating Image")
            }
        })
    if (showRateDialog) {
        AlertDialog(onDismissRequest = { showRateDialog = false },
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
                    val manager = ReviewManagerFactory.create(activity!!.applicationContext)
                    manager.requestReviewFlow().apply {
                        addOnCompleteListener { request ->
                            if (request.isSuccessful) {
                                val reviewInfo = request.result
                                val flow = manager.launchReviewFlow(activity, reviewInfo)
                                flow.addOnCompleteListener { _ ->
                                    Logger.info("Review finished")
                                }
                            } else {
                                Logger.info("Unable to show review")
                            }
                        }
                    }
                }) {
                    Text("Rate App")
                }
            },
            dismissButton = {
                Button(onClick = { showRateDialog = false }) {
                    Text(text = "Dismiss")
                }
            }
        )
    }
}