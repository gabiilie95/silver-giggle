package com.ilieinc.donotdisturbsync.presentation.ui.component

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Text
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.ScalingLazyColumn
import com.ilieinc.common.viewmodel.DoNotDisturbSyncCardViewModel
import com.ilieinc.common.viewmodel.base.CardViewModel
import com.ilieinc.donotdisturbsync.presentation.theme.Typography

@Composable
fun ActionCard(
    viewModel: CardViewModel = viewModel(),
    content: @Composable () -> Unit
) {
    val title by viewModel.title.collectAsState()
    val permissionRequired by remember { mutableStateOf(false) } //viewModel.permissionRequired.collectAsState()

    ScalingLazyColumn(
        Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = Typography.title1,
                    fontWeight = FontWeight.Bold
                )
//            Button(onClick = { viewModel.showHelpDialog.tryEmit(true) }) {
//                Text(text = "Help")
//            }
            }
            if (permissionRequired) {
                Button(
//                    modifier = Modifier.align(Alignment.End),
                    onClick = { viewModel.requestPermission() }) {
                    Text(text = "Get Started")
                }
            } else {
                content()
            }
        }
    }
}

@Preview
@Composable
fun ActionCardPreview() {
    ActionCard(DoNotDisturbSyncCardViewModel(Application())) {

    }
}