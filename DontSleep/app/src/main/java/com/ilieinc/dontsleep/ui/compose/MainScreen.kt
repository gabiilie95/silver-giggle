@file:OptIn(ExperimentalMaterial3Api::class)

package com.ilieinc.dontsleep.ui.compose

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ilieinc.dontsleep.receiver.DeviceAdminReceiver
import com.ilieinc.dontsleep.ui.compose.component.ActionCard
import com.ilieinc.dontsleep.ui.compose.component.DontSleepTopAppBar
import com.ilieinc.dontsleep.ui.theme.AppTheme
import com.ilieinc.dontsleep.viewmodel.DontSleepCardViewModel
import com.ilieinc.dontsleep.viewmodel.SleepCardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val activity = (LocalContext.current as? Activity)
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { DontSleepTopAppBar() },
        bottomBar = {
            Button(
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth(),
                onClick = { activity?.finish() }) {
                Text("Close")
            }
        }
    ) { paddingValues ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                DontSleepTimerCard()
                SleepTimerCard()
            }
        }
    }
}

@Composable
fun DontSleepTimerCard(viewModel: DontSleepCardViewModel = viewModel()) {
    val model = remember { viewModel }
    ActionCard(model)
}

@Composable
fun SleepTimerCard(viewModel: SleepCardViewModel = viewModel()) {
    val model = remember { viewModel }
    val permissionGranted by DeviceAdminReceiver.permissionGranted.collectAsState()
    model.permissionRequired.tryEmit(!permissionGranted)
    ActionCard(model)
}

@Preview
@Composable
fun MainScreenPreview() {
    AppTheme {
        MainScreen()
    }
}