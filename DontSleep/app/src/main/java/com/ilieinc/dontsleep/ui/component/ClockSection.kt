package com.ilieinc.dontsleep.ui.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.ilieinc.dontsleep.R
import com.ilieinc.dontsleep.ui.model.CardUiEvent
import com.ilieinc.dontsleep.ui.model.CardUiEvent.*
import com.ilieinc.dontsleep.ui.model.CardUiState
import com.ilieinc.dontsleep.ui.model.common.ClockState
import com.ilieinc.dontsleep.ui.model.common.ClockState.EditMode
import com.ilieinc.dontsleep.util.previewprovider.ClockSectionPreviewProvider
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClockSection(
    state: CardUiState,
    onEvent: (CardUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SwitchModesButton(
                state = state,
                onEvent = onEvent
            )
            if (state.clockState.editMode != null) {
                SwitchTimePickerMode(
                    state = state.clockState,
                    onEvent = onEvent
                )
            }
        }
        val editMode = state.clockState.editMode
        if (editMode != null) {
            val (initialHour, initialMinutes) = when (editMode) {
                EditMode.ADD -> {
                    with(Calendar.getInstance()) {
                        val hour = get(Calendar.HOUR_OF_DAY)
                        val minutes = get(Calendar.MINUTE)
                        hour to minutes
                    }
                }

                EditMode.EDIT -> {
                    with(state.selectedTime) {
                        val hour = this?.hour ?: 0
                        val minutes = this?.minute ?: 0
                        hour to minutes
                    }
                }
            }
            val timePickerState = rememberTimePickerState(
                initialHour = initialHour,
                initialMinute = initialMinutes
            )
            key(state.clockState) {
                timePickerState.is24hour = state.clockState.is24hour
                when (state.clockState.timepickerMode) {
                    ClockState.TimepickerMode.DIGITAL_INPUT -> {
                        TimeInput(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(vertical = 16.dp),
                            state = timePickerState
                        )
                    }

                    ClockState.TimepickerMode.CLOCK_PICKER -> {
                        TimePicker(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            state = timePickerState
                        )
                    }
                }
            }
            ManageSavedTimesButton(
                modifier = Modifier.fillMaxWidth(),
                state = state.clockState,
                timePickerState = timePickerState,
                editMode = editMode,
                enabled = state.editControlsEnabled,
                onEvent = onEvent
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
    }
}

@Composable
private fun SwitchTimePickerMode(
    state: ClockState,
    onEvent: (CardUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        modifier = modifier,
        onClick = { onEvent(OnSwitchTimePickerModeButtonClick(state.timepickerMode)) }
    ) {
        Text(
            stringResource(
                R.string.switch_timepicker_mode, when (state.timepickerMode) {
                    ClockState.TimepickerMode.DIGITAL_INPUT -> stringResource(R.string.clock_input)
                    ClockState.TimepickerMode.CLOCK_PICKER -> stringResource(R.string.digital_input)
                }
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ManageSavedTimesButton(
    state: ClockState,
    timePickerState: TimePickerState,
    editMode: EditMode,
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
            onClick = { onEvent(OnCancelButtonClick) }
        ) {
            Text(text = stringResource(R.string.cancel))
        }

        Change24HourButtonSwitch(
            state = state,
            onEvent = onEvent
        )

        Button(
            enabled = enabled,
            onClick = {
                onEvent(
                    OnConfirmEditClick(
                        hour = timePickerState.hour,
                        minute = timePickerState.minute,
                        editMode = editMode
                    )
                )
            }
        ) {
            Text(
                stringResource(
                    when (editMode) {
                        EditMode.ADD -> {
                            R.string.save
                        }

                        EditMode.EDIT -> {
                            R.string.update
                        }
                    }
                )
            )
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
                .padding(horizontal = 8.dp)
                .wrapContentSize(Alignment.TopStart),
            state = state,
            enabled = enabled,
            onEvent = onEvent
        )
        if (state.selectedTime != null) {
            IconButton(
                enabled = enabled,
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                onClick = { onEvent(OnEditSavedTimeClick) }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = null
                )
            }
        }
        if (savedTimes.isNotEmpty()) {
            if (state.selectedTime != null) {
                IconButton(
                    enabled = enabled,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    onClick = { onEvent(OnDeleteButtonClick) }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = null
                    )
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
                state.selectedTime?.getFormattedTime(state.is24hour)
                    ?: stringResource(R.string.select_time)
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
                val itemBackground = if (item == state.selectedTime) {
                    MaterialTheme.colorScheme.primary
                } else {
                    Color.Unspecified
                }
                DropdownMenuItem(
                    modifier = Modifier
                        .widthIn(min = 180.dp)
                        .background(itemBackground),
                    text = { Text(item.getFormattedTime(state.is24hour)) },
                    onClick = { onEvent(OnSavedTimeSelectionChange(item)) },
                    colors = MenuDefaults.itemColors(
                        textColor = MaterialTheme.colorScheme.contentColorFor(itemBackground)
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Change24HourButtonSwitch(
    state: ClockState,
    onEvent: (CardUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(stringResource(R.string.twelve_hour))
        Switch(
            checked = state.is24hour,
            onCheckedChange = {
                onEvent(On24HourModeChange(it))
            },
        )
        Text(stringResource(R.string.twenty_four_hour))
    }
}

@Preview
@Composable
fun ClockSectionPreview(@PreviewParameter(ClockSectionPreviewProvider::class) state: CardUiState) {
    ClockSection(
        state = state,
        onEvent = {}
    )
}
