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
import com.ilieinc.dontsleep.ui.model.CardUiEvent
import com.ilieinc.dontsleep.ui.model.CardUiState

@Composable
fun ActionCard(
    state: CardUiState,
    onEvent: (CardUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    with(state) {
        ThemedCard(
            modifier = modifier,
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
                    Button(onClick = { onEvent(CardUiEvent.ChangeHelpDialogVisibility(true)) }) {
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
                        onClick = { onEvent(CardUiEvent.ChangePermissionDialogVisibility(true)) }
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
                            onCheckedChange = { onEvent(CardUiEvent.StatusToggleChanged(it)) }
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
                                onCheckedChange = { onEvent(CardUiEvent.AutoOffToggleChanged(it)) }
                            )
                        }
                    }
                    if (timeoutEnabled) {
                        TimeoutSection(
                            modifier = Modifier.fillMaxWidth(),
                            state = state,
                            onUpdateTime = { hour, minute ->
                                onEvent(CardUiEvent.TimeoutTimeChanged(hour, minute))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TimeoutSection(
    state: CardUiState,
    onUpdateTime: (hour: Int, minute: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    with(state) {
        Row(
            modifier = modifier,
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
            if (hours != null && minutes != null) {
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
}

@Preview
@Composable
fun ActionCardPreview() {
    ActionCard(
        state = CardUiState(
            title = "Action Card",
            hours = 1,
            minutes = 30,
            timeoutEnabled = true,
            enabled = false,
            showTimeoutSectionToggle = true,
            timeoutSectionToggleEnabled = true,
            showPermissionDialog = false,
            showHelpDialog = false,
            permissionRequired = false
        ),
        onEvent = {}
    )
}