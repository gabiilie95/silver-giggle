package com.ilieinc.donotdisturbsync.viewmodel

import android.app.Application
import com.ilieinc.donotdisturbsync.util.PermissionHelper
import com.ilieinc.donotdisturbsync.viewmodel.base.HelpDialogViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class DoNotDisturbSyncHelpDialogViewModel(
    showDialog: MutableStateFlow<Boolean>,
    application: Application
) :
    HelpDialogViewModel(showDialog, application) {
    init {
        title.tryEmit("Do Not Disturb Sync Help")
        setDetails()
        revokeButtonText.tryEmit("Revoke Draw Over Permission")
    }

    private fun setDetails() {
        description.tryEmit(buildString {
            append("This feature enables you to turn off your screen timeout with the click of a button.")
            if (PermissionHelper.hasNotificationPolicyAccessPermission(getApplication())) {
                append("\nYou can revoke the draw over permission from this dialog.")
                showRevokePermissionButton.tryEmit(true)
            }
        })
    }

    override fun revokePermission() {
        PermissionHelper.requestNotificationPolicyAccessPermission(getApplication())
        showDialog.tryEmit(false)
    }
}
