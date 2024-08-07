//package com.ilieinc.donotdisturbsync.ui.compose.component
//
//import androidx.annotation.RequiresApi
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Info
//import androidx.compose.material3.Button
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//
//@RequiresApi(33)
//@Composable
//fun RequestNotificationButton(viewModel: NotificationButtonDialogViewModel) {
//    val showDialog by viewModel.showDialog.collectAsState()
//    Button(
//        modifier = Modifier.fillMaxWidth(),
//        onClick = { viewModel.requestPermission() }) {
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Text(text = "Allow Notifications")
//            IconButton(onClick = { viewModel.showDialog.tryEmit(true) }) {
//                Icon(imageVector = Icons.Default.Info, contentDescription = null)
//            }
//        }
//    }
//    if (showDialog) {
//        NotificationInfoDialog(viewModel)
//    }
//}