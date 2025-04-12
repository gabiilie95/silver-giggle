package com.ilieinc.dontsleep.ui.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.rounded.AddCircleOutline
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ilieinc.dontsleep.R
import com.ilieinc.dontsleep.ui.model.CardUiEvent
import com.ilieinc.dontsleep.ui.model.CardUiEvent.OnAddButtonClick
import com.ilieinc.dontsleep.ui.model.CardUiEvent.OnCancelButtonClick
import com.ilieinc.dontsleep.ui.model.CardUiEvent.OnDeleteButtonClick
import com.ilieinc.dontsleep.ui.model.CardUiEvent.OnExpandedDropdownChanged
import com.ilieinc.dontsleep.ui.model.CardUiEvent.OnSaveButtonClick
import com.ilieinc.dontsleep.ui.model.CardUiEvent.OnSavedTimeSelectionChange
import com.ilieinc.dontsleep.ui.model.CardUiState
import com.ilieinc.dontsleep.ui.model.common.ClockState
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClockSection(
    state: CardUiState,
    onEvent: (CardUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val calendar = remember { Calendar.getInstance() }
    Column(modifier = modifier) {
        val timePickerState = rememberTimePickerState(
            initialHour = calendar[Calendar.HOUR_OF_DAY],
            initialMinute = calendar[Calendar.MINUTE],
            is24Hour = false
        )
        SwitchModesButton(
            state = state,
            onEvent = onEvent
        )
        if (state.clockState.isAddingNewTime) {
            TimeInput(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 16.dp),
                state = timePickerState
            )
        } else {
            SavedTimesSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                state = state.clockState,
                enabled = state.editControlsEnabled,
                onEvent = onEvent
            )
        }
        if (state.clockState.isAddingNewTime) {
            ManageSavedTimesButton(
                modifier = Modifier.fillMaxWidth(),
                enabled = state.editControlsEnabled,
                timePickerState = timePickerState,
                onEvent = onEvent
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ManageSavedTimesButton(
    timePickerState: TimePickerState,
    enabled: Boolean,
    onEvent: (CardUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            enabled = enabled,
            onClick = {
                onEvent(
                    OnSaveButtonClick(
                        hour = timePickerState.hour,
                        minute = timePickerState.minute,
                        isAfternoon = timePickerState.isAfternoon
                    )
                )
            }
        ) {
            Text("Save")
        }
        Button(
            onClick = { onEvent(OnCancelButtonClick) }
        ) {
            Text(text = stringResource(R.string.cancel))
        }
    }
}

@Composable
private fun SavedTimesSection(
    state: ClockState,
    enabled: Boolean,
    onEvent: (CardUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier then Modifier.animateContentSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val savedTimes = state.savedTimes
        IconButton(
            enabled = enabled,
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            ),
            onClick = { onEvent(OnAddButtonClick) }
        ) {
            Icon(
                imageVector = Icons.Default.AddCircleOutline,
                contentDescription = null
            )
        }
        SavedTimesDropdown(
            modifier = Modifier
                .weight(1f)
                .wrapContentSize(Alignment.TopStart),
            state = state,
            enabled = enabled,
            onEvent = onEvent
        )
        if (savedTimes.isNotEmpty()) {
            if (state.selectedTime != null) {
                Button(
                    modifier = Modifier.padding(start = 8.dp),
                    enabled = enabled,
                    onClick = { onEvent(OnDeleteButtonClick) }
                ) {
                    Text(text = stringResource(R.string.delete))
                }
            }
        }
    }
}

@Composable
private fun SavedTimesDropdown(
    state: ClockState,
    enabled: Boolean,
    onEvent: (CardUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier then Modifier
            .clip(RoundedCornerShape(12.dp))
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            value = if (state.savedTimes.any()) {
                state.selectedTime?.formattedTime ?: stringResource(R.string.select_time)
            } else {
                stringResource(R.string.no_saved_times)
            },
            enabled = enabled,
            interactionSource = interactionSource
                .also { interactionSource ->
                    LaunchedEffect(interactionSource) {
                        interactionSource.interactions.collect {
                            if (it is PressInteraction.Release) {
                                onEvent(OnExpandedDropdownChanged(true))
                            }
                        }
                    }
                },
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                IconButton(
                    enabled = enabled,
                    onClick = { onEvent(OnExpandedDropdownChanged(true)) }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowDropDown,
                        contentDescription = null
                    )
                }
            }
        )
        DropdownMenu(
            expanded = enabled && state.isDropdownExpanded,
            onDismissRequest = { onEvent(OnExpandedDropdownChanged(false)) },
        ) {
            state.savedTimes.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item.formattedTime) },
                    onClick = { onEvent(OnSavedTimeSelectionChange(item)) }
                )
            }
        }
    }
}