package com.ilieinc.donotdisturbsync.ui.compose

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ilieinc.donotdisturbsync.ui.compose.component.ActionCard
import com.ilieinc.donotdisturbsync.ui.compose.component.OnLifecycleEvent
import com.ilieinc.donotdisturbsync.viewmodel.DoNotDisturbSyncCardViewModel

@Composable
fun DoNotDisturbSyncCard(viewModel: DoNotDisturbSyncCardViewModel = viewModel()) {
    val model = remember { viewModel }
    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> model.refreshPermissionState()
            else -> {}
        }
    }
    val phoneToWearableSyncEnabled by model.phoneToWearableSyncEnabled.collectAsState()
    val wearableToPhoneSyncEnabled by model.wearableToPhoneSyncEnabled.collectAsState()
    ActionCard(model) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 25.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Phone To Wearable Sync Enabled",
                    fontWeight = FontWeight.Bold
                )
                Switch(
                    checked = phoneToWearableSyncEnabled,
                    onCheckedChange = {
                        viewModel.phoneToWearableSyncEnabled.value = !phoneToWearableSyncEnabled
                    }
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Wearable To Phone Sync Enabled",
                    fontWeight = FontWeight.Bold
                )
                Switch(
                    checked = wearableToPhoneSyncEnabled,
                    onCheckedChange = {
                        viewModel.wearableToPhoneSyncEnabled.value = !wearableToPhoneSyncEnabled
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun DoNotDisturbSyncCardPreview() {
    DoNotDisturbSyncCard(DoNotDisturbSyncCardViewModel(Application()))
}