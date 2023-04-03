package com.ilieinc.dontsleep.viewmodel

import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.annotation.RequiresApi
import com.ilieinc.dontsleep.MainActivity
import com.ilieinc.core.util.PermissionHelper
import com.ilieinc.core.viewmodel.PermissionDialogViewModel
import kotlinx.coroutines.flow.MutableStateFlow

@RequiresApi(33)
class NotificationButtonDialogViewModel(
    showDialog: MutableStateFlow<Boolean>,
    activity: MainActivity,
    private val notificationPermissionRequest: ManagedActivityResultLauncher<String, Boolean>
) : PermissionDialogViewModel(showDialog, activity.application) {

    override fun requestPermission() {
        PermissionHelper.requestNotificationPermission(notificationPermissionRequest)
    }
}