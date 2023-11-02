package com.ilieinc.dontsleep.viewmodel

import android.app.Application
import com.ilieinc.core.util.DeviceAdminHelper
import com.ilieinc.dontsleep.viewmodel.base.HelpDialogViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class ScreenTimeoutCardHelpDialogViewModel(
    onDismissRequestedCallback: () -> Unit,
    application: Application,
) : HelpDialogViewModel(onDismissRequestedCallback, application) {
    init {
        title = "Sleep! Help"
        setDetails()
        revokeButtonText = "Revoke Admin Permission"
    }

    private fun setDetails() {
        description = buildString {
            append("When this feature is enabled you will not be able to uninstall the application before first revoking the administrator permission.")
            if (DeviceAdminHelper.adminPermissionGranted()) {
                append("\nYou can revoke the draw over permission from this dialog.")
                showRevokePermissionButton = true
            }
        }
    }

    override fun revokePermission() {
        DeviceAdminHelper.removeActiveAdmin()
        onDismissRequested()
    }
}
