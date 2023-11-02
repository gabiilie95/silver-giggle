package com.ilieinc.dontsleep.ui.component

import android.app.Application
import android.view.LayoutInflater
import android.widget.TimePicker
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ilieinc.core.ui.components.ThemedCard
import com.ilieinc.dontsleep.R
import com.ilieinc.core.ui.theme.AppTypography
import com.ilieinc.dontsleep.viewmodel.ScreenTimeoutCardViewModel
import com.ilieinc.dontsleep.viewmodel.base.CardViewModel

@Composable
fun ActionCard(
    viewModel: CardViewModel = viewModel()
) {
    with(viewModel) {
        val enabled by this.enabled.collectAsState()
        ThemedCard(
            modifier = Modifier.padding(5.dp).animateContentSize(),
            title = title,
            titleBar = {
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
                    Button(onClick = viewModel::onShowHelpDialog) {
                        Text(text = "Help")
                    }
                }
            }
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                if (permissionRequired) {
                    Button(
                        modifier = Modifier.align(Alignment.End),
                        onClick = viewModel::onShowPermissionDialog) {
                        Text(text = "Get Started")
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Status",
                            fontWeight = FontWeight.Bold
                        )
                        Switch(
                            checked = enabled,
                            onCheckedChange = { viewModel.enabled.tryEmit(!enabled) }
                        )
                    }
                    if (showTimeoutSectionToggle) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Enable Auto-Off",
                                fontWeight = FontWeight.Bold
                            )
                            Switch(
                                checked = timeoutEnabled,
                                onCheckedChange = viewModel::timeoutChanged
                            )
                        }
                    }
                    if (timeoutEnabled) {
                        TimeoutSection(viewModel)
                    }
                }
            }
        }
        if (showHelpDialog) {
            CardHelpDialog(viewModel)
        }
        if (showPermissionDialog) {
            CardPermissionDialog(viewModel)
        }
    }
}

@Composable
fun TimeoutSection(viewModel: CardViewModel) {
    with(viewModel) {
        val enabled by this.enabled.collectAsState()
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Timeout",
                    fontWeight = FontWeight.Bold
                )
                Text(text = "(Hours, Minutes)")
            }
            AndroidView(
                factory = { context ->
                    val layout = LayoutInflater.from(context)
                        .inflate(R.layout.layout_time_picker, null)
                    val timePicker =
                        layout.findViewById<TimePicker>(R.id.time_picker).apply {
                            isEnabled = !enabled
                            setIs24HourView(true)
                            this.hour = hours
                            this.minute = minutes
                            this.setOnTimeChangedListener { _, hours, minutes ->
                                viewModel.updateTime(hours, minutes)
                            }
                        }
                    return@AndroidView timePicker
                },
                update = { timePicker ->
                    timePicker.isEnabled = !enabled
                }
            )
        }
    }
}

@Preview
@Composable
fun ActionCardPreview() {
    ActionCard(ScreenTimeoutCardViewModel(Application()))
}