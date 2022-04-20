@file:OptIn(ExperimentalMaterial3Api::class)

package com.ilieinc.dontsleep.ui.compose.component

import android.app.Activity
import android.view.LayoutInflater
import android.widget.TimePicker
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ilieinc.dontsleep.R
import com.ilieinc.dontsleep.ui.theme.AppTypography
import com.ilieinc.dontsleep.viewmodel.*
import com.ilieinc.dontsleep.viewmodel.base.CardViewModel
import com.ilieinc.dontsleep.viewmodel.base.HelpDialogViewModel
import com.ilieinc.dontsleep.viewmodel.base.PermissionDialogViewModel
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun ActionCard(
    viewModel: CardViewModel = viewModel()
) {
    val title by viewModel.title.collectAsState()
    val enabled by viewModel.enabled.collectAsState()
    val hours by viewModel.hours.collectAsState()
    val minutes by viewModel.minutes.collectAsState()
    val showHelp by viewModel.showHelpDialog.collectAsState()
    val showPermissionDialog by viewModel.showPermissionDialog.collectAsState()
    val permissionRequired by viewModel.permissionRequired.collectAsState()

    Card(
        Modifier
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(5.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = AppTypography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Button(onClick = { viewModel.showHelpDialog.tryEmit(true) }) {
                    Text(text = "Help")
                }
            }
            if (permissionRequired) {
                Button(
                    modifier = Modifier.align(Alignment.End),
                    onClick = { viewModel.showPermissionDialog.tryEmit(true) }) {
                    Text(text = "Get Started")
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Status")
                    Switch(
                        checked = enabled,
                        onCheckedChange = { viewModel.enabled.tryEmit(!enabled) })
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Timeout")
                        Text(text = "(Hours, Minutes)")
                    }
                    AndroidView(
                        factory = { context ->
                            val layout = LayoutInflater.from(context)
                                .inflate(R.layout.layout_time_picker, null)
                            val timePicker =
                                layout.findViewById<TimePicker>(R.id.time_picker).apply {
                                    setIs24HourView(true)
                                    this.hour = hours
                                    this.minute = minutes
                                    this.setOnTimeChangedListener { _, hours, minutes ->
                                        viewModel.updateTime(hours, minutes)
                                    }
                                }
                            return@AndroidView timePicker
                        }
                    )
                }
            }
        }
    }
    if (showHelp) {
        CardHelpDialog(viewModel.showHelpDialog, viewModel)
    }
    if (showPermissionDialog) {
        CardPermissionDialog(viewModel.showPermissionDialog, viewModel)
    }
}