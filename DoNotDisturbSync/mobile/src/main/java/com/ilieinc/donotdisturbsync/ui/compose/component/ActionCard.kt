package com.ilieinc.donotdisturbsync.ui.compose.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ilieinc.donotdisturbsync.ui.theme.AppTypography
import com.ilieinc.donotdisturbsync.viewmodel.base.CardViewModel

@Composable
fun ActionCard(
    viewModel: CardViewModel = viewModel(),
    content: @Composable () -> Unit
) {
    val title by viewModel.title.collectAsState()
    val showHelp by viewModel.showHelpDialog.collectAsState()
    val showPermissionDialog by viewModel.showPermissionDialog.collectAsState()
    val permissionRequired by viewModel.permissionRequired.collectAsState()

    Card(
        Modifier
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
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
                Button(onClick = { viewModel.showHelpDialog.tryEmit(true) }) {
                    Text(text = "Help")
                }
            }
            if (permissionRequired) {
                Button(
                    modifier = Modifier.align(Alignment.End),
                    onClick = { viewModel.showPermissionDialog.tryEmit(true) }) {
                    Text(text = "Get Started")
                }
            } else {
                content()
            }
        }
    }
    if (showHelp) {
        CardHelpDialog(viewModel.showHelpDialog, viewModel)
    }
    if (showPermissionDialog) {
        CardPermissionDialog(viewModel.showPermissionDialog, viewModel)
    }
}

@Preview
@Composable
fun ActionCardPreview() {
//    ActionCard(ScreenTimeoutCardViewModel(Application()))
}