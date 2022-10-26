package com.ilieinc.donotdisturbsync.presentation.ui

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.Switch
import androidx.wear.compose.material.Text
import com.ilieinc.common.viewmodel.DoNotDisturbSyncCardViewModel
import com.ilieinc.donotdisturbsync.presentation.theme.Typography
import com.ilieinc.donotdisturbsync.presentation.ui.component.ActionCard

@Composable
fun DoNotDisturbSyncCard(viewModel: DoNotDisturbSyncCardViewModel = viewModel()) {
    val model = remember { viewModel }
//    OnLifecycleEvent { _, event ->
//        when (event) {
//            Lifecycle.Event.ON_RESUME -> model.refreshPermissionState()
//            else -> {}
//        }
//    }
    val phoneToWearableSyncEnabled by model.phoneToWearableSyncEnabled.collectAsState()
    val wearableToPhoneSyncEnabled by model.wearableToPhoneSyncEnabled.collectAsState()
    ActionCard(model) {
        Column(
            Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "Phone To Wearable Sync Enabled",
                fontWeight = FontWeight.Bold
            )
            Switch(
                modifier = Modifier.align(Alignment.End),
                checked = phoneToWearableSyncEnabled,
                onCheckedChange = {
                    viewModel.phoneToWearableSyncEnabled.value = !phoneToWearableSyncEnabled
                }
            )
            Text(
                text = "Wearable To Phone Sync Enabled",
                fontWeight = FontWeight.Bold
            )
            Switch(
                modifier = Modifier.align(Alignment.End),
                checked = wearableToPhoneSyncEnabled,
                onCheckedChange = {
                    viewModel.wearableToPhoneSyncEnabled.value = !wearableToPhoneSyncEnabled
                }
            )
        }
    }
}

@Preview
@Composable
fun DoNotDisturbSyncCardPreview() {
    DoNotDisturbSyncCard(DoNotDisturbSyncCardViewModel(Application()))
}