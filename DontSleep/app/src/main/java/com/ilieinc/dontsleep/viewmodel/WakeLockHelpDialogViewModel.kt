package com.ilieinc.dontsleep.viewmodel

import android.app.Application
import com.ilieinc.dontsleep.util.PermissionHelper
import com.ilieinc.dontsleep.viewmodel.base.HelpDialogViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class WakeLockHelpDialogViewModel(
    showDialog: MutableStateFlow<Boolean>,
    application: Application
) :
    HelpDialogViewModel(showDialog, application) {
    init {
        title.tryEmit("Don't Sleep! Help")
        setDetails()
        revokeButtonText.tryEmit("Revoke Draw Over Permission")
    }

    private fun setDetails() {
        description.tryEmit(buildString {
            append("This feature enables you to turn off your screen timeout with the click of a button.")
            if (PermissionHelper.hasDrawOverPermission(getApplication())) {
                append("\nYou can revoke the draw over permission from this dialog.")
                showRevokePermissionButton.tryEmit(true)
            }
        })
    }

    override fun revokePermission() {
        PermissionHelper.requestDrawOverPermission(getApplication())
        showDialog.tryEmit(false)
    }
}
