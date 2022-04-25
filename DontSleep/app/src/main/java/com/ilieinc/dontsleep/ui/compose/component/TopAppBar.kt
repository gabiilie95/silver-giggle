package com.ilieinc.dontsleep.ui.compose.component

import android.app.Activity
import android.content.Intent
import android.net.Uri
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
import androidx.core.content.ContextCompat.startActivity
import com.google.android.play.core.review.ReviewManagerFactory
import com.ilieinc.dontsleep.util.Logger
import com.ilieinc.dontsleep.util.StateHelper
import com.ilieinc.dontsleep.viewmodel.RatingDialogViewModel
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun DontSleepTopAppBar() {
    val context = LocalContext.current
    val activity = (context as Activity)
    val useDynamicColors by StateHelper.useDynamicColors.collectAsState()
    val showRateDialog = remember { MutableStateFlow(false) }

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
        IconButton(onClick = { showRateDialog.tryEmit(true) }) {
            Icon(Icons.Outlined.StarRate, "Rating Image")
        }
    })
    if (showRateDialog.collectAsState().value) {
        RatingDialog(RatingDialogViewModel(showRateDialog, activity.application))
    }
}