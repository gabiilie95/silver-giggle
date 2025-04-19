package com.ilieinc.dontsleep.viewmodel

import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.annotation.RequiresApi
import com.ilieinc.core.util.PermissionHelper
import com.ilieinc.core.viewmodel.PermissionDialogViewModel
import com.ilieinc.dontsleep.MainActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@RequiresApi(33)
class NotificationButtonDialogViewModel(
    activity: MainActivity,
    private val notificationPermissionRequest: ManagedActivityResultLauncher<String, Boolean>
) : PermissionDialogViewModel(activity.application) {

    private val _showDialog = MutableStateFlow(false)
    val showDialog = _showDialog.asStateFlow()

    override fun requestPermission() {
        PermissionHelper.requestNotificationPermission(notificationPermissionRequest)
    }

    fun onShowRequested() {
        _showDialog.update { true }
    }

    override fun onDismissRequested() {
        _showDialog.update { false }
    }
}