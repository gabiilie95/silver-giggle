package com.ilieinc.dontsleep.ui.component

import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.ilieinc.core.ui.components.NotificationInfoDialog
import com.ilieinc.dontsleep.R
import com.ilieinc.dontsleep.viewmodel.NotificationButtonDialogViewModel

@RequiresApi(33)
@Composable
fun RequestNotificationButton(viewModel: NotificationButtonDialogViewModel) {
    with(viewModel) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { requestPermission() }) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = stringResource(R.string.allow_notifications))
                IconButton(onClick = ::onShowRequested) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = null)
                }
            }
        }
        val showDialog by showDialog.collectAsState()
        if (showDialog) {
            NotificationInfoDialog(viewModel)
        }
    }
}