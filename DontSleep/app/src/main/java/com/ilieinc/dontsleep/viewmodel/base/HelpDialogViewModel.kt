package com.ilieinc.dontsleep.viewmodel.base

import android.app.Application
import com.ilieinc.core.viewmodel.base.DialogViewModel
import kotlinx.coroutines.flow.MutableStateFlow

abstract class HelpDialogViewModel(
    showDialog: MutableStateFlow<Boolean>,
    application: Application
) :
    DialogViewModel(showDialog, application) {
    val showRevokePermissionButton = MutableStateFlow(false)
    val revokeButtonText = MutableStateFlow("Revoke Permission")

    open fun revokePermission() { }
}