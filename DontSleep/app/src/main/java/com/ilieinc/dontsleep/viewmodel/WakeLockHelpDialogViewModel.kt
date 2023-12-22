package com.ilieinc.dontsleep.viewmodel

import android.app.Application
import com.ilieinc.core.util.PermissionHelper
import com.ilieinc.dontsleep.viewmodel.base.HelpDialogViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class WakeLockHelpDialogViewModel(
    onDismissRequestedCallback: () -> Unit,
    application: Application
) : HelpDialogViewModel(onDismissRequestedCallback, application) {
    init {
        setDetails()
    }

    private fun setDetails() {
        uiModel.update {
            it.copy(
                title = "Don't Sleep! Help",
                description = buildString {
                    append("This feature enables you to turn off your screen timeout with the click of a button.")
                    if (PermissionHelper.hasDrawOverPermission(getApplication())) {
                        append("\nYou can revoke the draw over permission from this dialog.")
                    }
                },
                revokeButtonText = "Revoke Draw Over Permission",
                showRevokePermissionButton = PermissionHelper.hasDrawOverPermission(getApplication())
            )
        }

    }

    override fun revokePermission() {
        PermissionHelper.requestDrawOverPermission(getApplication())
        onDismissRequested()
    }
}
