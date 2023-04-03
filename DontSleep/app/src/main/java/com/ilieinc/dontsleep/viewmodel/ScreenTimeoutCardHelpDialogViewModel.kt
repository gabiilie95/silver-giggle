package com.ilieinc.dontsleep.viewmodel

import android.app.Application
import com.ilieinc.core.util.DeviceAdminHelper
import com.ilieinc.dontsleep.viewmodel.base.HelpDialogViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class ScreenTimeoutCardHelpDialogViewModel(
    showDialog: MutableStateFlow<Boolean>,
    application: Application,
) :
    HelpDialogViewModel(showDialog, application) {
    init {
        title.tryEmit("Sleep! Help")
        setDetails()
        revokeButtonText.tryEmit("Revoke Admin Permission")
    }

    private fun setDetails() {
        description.tryEmit(buildString {
            append("When this feature is enabled you will not be able to uninstall the application before first revoking the administrator permission.")
            if (DeviceAdminHelper.adminPermissionGranted()) {
                append("\nYou can revoke the draw over permission from this dialog.")
                showRevokePermissionButton.tryEmit(true)
            }
        })
    }

    override fun revokePermission() {
        DeviceAdminHelper.removeActiveAdmin()
        showDialog.tryEmit(false)
    }
}
