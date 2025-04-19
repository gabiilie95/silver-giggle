package com.ilieinc.dontsleep.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.ilieinc.core.ui.components.ThemedCard
import com.ilieinc.core.ui.theme.AppTypography
import com.ilieinc.dontsleep.R
import com.ilieinc.dontsleep.ui.model.CardUiEvent
import com.ilieinc.dontsleep.ui.model.CardUiEvent.OnAutoOffToggleChange
import com.ilieinc.dontsleep.ui.model.CardUiEvent.OnChangeHelpDialogVisibility
import com.ilieinc.dontsleep.ui.model.CardUiEvent.OnChangePermissionDialogVisibility
import com.ilieinc.dontsleep.ui.model.CardUiEvent.OnStatusToggleChange
import com.ilieinc.dontsleep.ui.model.CardUiState
import com.ilieinc.dontsleep.util.previewprovider.TimePickerPreviewProvider

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
                    Button(onClick = { onEvent(OnChangeHelpDialogVisibility(true)) }) {
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
                when {
                    isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(vertical = 24.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                    }

                    permissionRequired -> {
                        Button(
                            modifier = Modifier.align(Alignment.End),
                            onClick = { onEvent(OnChangePermissionDialogVisibility(true)) }
                        ) {
                            Text(text = stringResource(R.string.get_started))
                        }
                    }

                    else -> {
                        SwitchRow(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(R.string.status),
                            checked = enabled,
                            enabled = enableButtonEnabled,
                            onCheckedChange = { onEvent(OnStatusToggleChange(it)) }
                        )
                        if (showTimeoutSectionToggle) {
                            SwitchRow(
                                modifier = Modifier.fillMaxWidth(),
                                text = stringResource(R.string.enable_auto_off),
                                checked = timeoutEnabled,
                                onCheckedChange = { onEvent(OnAutoOffToggleChange(it)) },
                                enabled = editControlsEnabled
                            )
                        }
                        if (timeoutEnabled) {
                            TimeSection(
                                modifier = Modifier.fillMaxWidth(),
                                state = state,
                                onEvent = onEvent
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SwitchRow(
    text: String,
    onCheckedChange: (Boolean) -> Unit,
    checked: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold
        )
        Switch(
            checked = checked,
            enabled = enabled,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun TimeSection(
    state: CardUiState,
    onEvent: (CardUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    with(state) {
        when (state.timeoutMode) {
            CardUiState.TimeoutMode.TIMEOUT -> TimeoutSection(
                modifier = modifier,
                state = state,
                onEvent = onEvent
            )

            CardUiState.TimeoutMode.CLOCK -> ClockSection(
                modifier = modifier,
                state = state,
                onEvent = onEvent
            )
        }
    }
}

@Preview
@Composable
fun ActionCardPreview(
    @PreviewParameter(TimePickerPreviewProvider::class)state: CardUiState
) {
    ActionCard(
        state = state,
        onEvent = {}
    )
}
