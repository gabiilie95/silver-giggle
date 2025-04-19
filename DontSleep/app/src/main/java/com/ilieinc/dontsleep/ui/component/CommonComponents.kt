package com.ilieinc.dontsleep.ui.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ilieinc.dontsleep.ui.model.CardUiEvent
import com.ilieinc.dontsleep.ui.model.CardUiEvent.OnTimeoutModeButtonClick
import com.ilieinc.dontsleep.ui.model.CardUiState

@Composable
fun SwitchModesButton(
    state: CardUiState,
    onEvent: (CardUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    when (state.timeoutMode) {
        CardUiState.TimeoutMode.TIMEOUT -> {
            OutlinedButton(
                modifier = modifier,
                contentPadding = PaddingValues(0.dp),
                enabled = state.editControlsEnabled,
                onClick = { onEvent(OnTimeoutModeButtonClick(state)) }
            ) {
                Icon(imageVector = Icons.Outlined.AccessTime, contentDescription = null)
            }
        }

        CardUiState.TimeoutMode.CLOCK -> {
            OutlinedButton(
                modifier = modifier,
                contentPadding = PaddingValues(0.dp),
                enabled = state.editControlsEnabled,
                onClick = { onEvent(OnTimeoutModeButtonClick(state)) }
            ) {
                Icon(imageVector = Icons.Outlined.Timer, contentDescription = null)
            }
        }
    }
}
