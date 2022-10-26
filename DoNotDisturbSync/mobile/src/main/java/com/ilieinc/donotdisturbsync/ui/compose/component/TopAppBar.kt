package com.ilieinc.donotdisturbsync.ui.compose.component

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoNotDisturb
import androidx.compose.material.icons.filled.DoNotDisturbAlt
import androidx.compose.material.icons.filled.DoNotDisturbOff
import androidx.compose.material.icons.filled.DoNotDisturbOn
import androidx.compose.material.icons.outlined.DoNotDisturbOn
import androidx.compose.material.icons.outlined.InvertColors
import androidx.compose.material.icons.outlined.InvertColorsOff
import androidx.compose.material.icons.outlined.StarRate
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ilieinc.donotdisturbsync.util.StateHelper
import com.ilieinc.donotdisturbsync.viewmodel.RatingDialogViewModel
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationTopAppBar() {
    val context = LocalContext.current
    val activity = (context as Activity)
    val useDynamicColors by StateHelper.useDynamicColors.collectAsState()
    val showRateDialog = remember { MutableStateFlow(false) }

    TopAppBar(title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Outlined.DoNotDisturbOn,
                "Title Icon",
                tint = MaterialTheme.colorScheme.contentColorFor(MaterialTheme.colorScheme.background)
            )
            Text(
                modifier = Modifier.padding(start = 5.dp),
                text = "Do Not Disturb Sync",
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