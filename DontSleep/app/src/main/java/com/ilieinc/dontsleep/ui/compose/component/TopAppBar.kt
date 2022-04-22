package com.ilieinc.dontsleep.ui.compose.component

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.outlined.InvertColors
import androidx.compose.material.icons.outlined.InvertColorsOff
import androidx.compose.material.icons.outlined.StarRate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.android.play.core.review.ReviewManagerFactory
import com.ilieinc.dontsleep.util.Logger
import com.ilieinc.dontsleep.util.StateHelper

@Composable
fun DontSleepTopAppBar() {
    val context = LocalContext.current
    val activity = (context as Activity)
    val useDynamicColors by StateHelper.useDynamicColors.collectAsState()
    var showRateDialog by remember { mutableStateOf(false) }

    SmallTopAppBar(title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.Smartphone,
                "Title Icon",
                tint = MaterialTheme.colorScheme.contentColorFor(MaterialTheme.colorScheme.background)
            )
            Text(
                modifier = Modifier.padding(start = 5.dp),
                text = "Don't Sleep!",
                fontWeight = FontWeight.Bold
            )
        }
    }, actions = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            IconButton(onClick = {
                StateHelper.setDynamicColorsEnabled(
                    context,
                    !useDynamicColors
                )
            }) {
                Icon(
                    if (useDynamicColors) Icons.Outlined.InvertColors
                    else Icons.Outlined.InvertColorsOff,
                    "Dynamic Colors Image"
                )
            }
        }
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
                    val manager = ReviewManagerFactory.create(context)
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