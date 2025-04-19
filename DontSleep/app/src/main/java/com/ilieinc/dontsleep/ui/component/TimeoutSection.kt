package com.ilieinc.dontsleep.ui.component

import android.view.LayoutInflater
import android.widget.TimePicker
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.viewinterop.AndroidView
import com.ilieinc.dontsleep.R
import com.ilieinc.dontsleep.ui.model.CardUiEvent
import com.ilieinc.dontsleep.ui.model.CardUiEvent.OnTimeoutTimeChange
import com.ilieinc.dontsleep.ui.model.CardUiState
import com.ilieinc.dontsleep.ui.model.common.TimeoutState

@Composable
fun TimeoutSection(
    state: CardUiState,
    onEvent: (CardUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        val selectedTime = state.selectedTime ?: return
        Row(
            modifier = Modifier.align(Alignment.CenterStart),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier) {
                Text(
                    text = stringResource(R.string.timeout),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.hours_minutes)
                )
            }
            AndroidView(
                modifier = Modifier.fillMaxWidth(),
                factory = { context ->
                    val layout = LayoutInflater.from(context)
                        .inflate(R.layout.layout_time_picker, null)
                    val timePicker =
                        layout.findViewById<TimePicker>(R.id.time_picker).apply {
                            isEnabled = !state.enabled
                            setIs24HourView(true)
                            this.hour = selectedTime.hour
                            this.minute = selectedTime.minute
                            this.setOnTimeChangedListener { _, hours, minutes ->
                                onEvent(OnTimeoutTimeChange(hour, minute))
                            }
                        }
                    return@AndroidView timePicker
                },
                update = { timePicker ->
                    with(timePicker) {
                        isEnabled = !state.enabled
                        hour = selectedTime.hour
                        minute = selectedTime.minute
                    }
                }
            )
        }
        SwitchModesButton(
            modifier = Modifier.align(Alignment.TopStart),
            state = state,
            onEvent = onEvent
        )
    }
}
