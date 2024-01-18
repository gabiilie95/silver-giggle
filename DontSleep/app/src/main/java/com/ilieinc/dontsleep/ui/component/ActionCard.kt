package com.ilieinc.dontsleep.ui.component

import android.view.LayoutInflater
import android.widget.TimePicker
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.ilieinc.core.ui.components.ThemedCard
import com.ilieinc.dontsleep.R
import com.ilieinc.core.ui.theme.AppTypography
import com.ilieinc.dontsleep.ui.model.CardUiModel

@Composable
fun ActionCard(
    uiModel: CardUiModel,
    onShowHelp: () -> Unit = {},
    onShowPermissionDialog: () -> Unit = {},
    autoOffChanged: (Boolean) -> Unit = {},
    onCheckedChanged: (Boolean) -> Unit = {},
    onUpdateTime: (hour: Int, minute: Int) -> Unit
) {
    with(uiModel) {
        ThemedCard(
            modifier = Modifier
                .padding(5.dp)
                .animateContentSize(),
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
                    Button(onClick = onShowHelp) {
                        Text(text = stringResource(R.string.help))
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
                        onClick = onShowPermissionDialog
                    ) {
                        Text(text = stringResource(R.string.get_started))
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.status),
                            fontWeight = FontWeight.Bold
                        )
                        Switch(
                            checked = enabled,
                            onCheckedChange = onCheckedChanged
                        )
                    }
                    if (showTimeoutSectionToggle) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.enable_auto_off),
                                fontWeight = FontWeight.Bold
                            )
                            Switch(
                                checked = timeoutEnabled,
                                enabled = timeoutSectionToggleEnabled,
                                onCheckedChange = autoOffChanged
                            )
                        }
                    }
                    if (timeoutEnabled) {
                        TimeoutSection(
                            uiModel = uiModel,
                            onUpdateTime = onUpdateTime
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TimeoutSection(
    uiModel: CardUiModel,
    onUpdateTime: (hour: Int, minute: Int) -> Unit
) {
    with(uiModel) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = stringResource(R.string.timeout),
                    fontWeight = FontWeight.Bold
                )
                Text(text = stringResource(R.string.hours_minutes))
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
                                onUpdateTime(hours, minutes)
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
    ActionCard(CardUiModel()) { _, _ -> }
}