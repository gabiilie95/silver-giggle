package com.ilieinc.dontsleep.viewmodel

import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.ilieinc.dontsleep.MainActivity
import com.ilieinc.core.util.PermissionHelper
import com.ilieinc.core.viewmodel.PermissionDialogViewModel
import kotlinx.coroutines.flow.MutableStateFlow

@RequiresApi(33)
class NotificationButtonDialogViewModel(
    activity: MainActivity,
    private val notificationPermissionRequest: ManagedActivityResultLauncher<String, Boolean>
) : PermissionDialogViewModel({}, activity.application) {

    var showDialog by mutableStateOf(false)
        private set

    override fun requestPermission() {
        PermissionHelper.requestNotificationPermission(notificationPermissionRequest)
    }

    fun onShowRequested() {
        showDialog = true
    }

    override fun onDismissRequested() {
        showDialog = false
    }
}