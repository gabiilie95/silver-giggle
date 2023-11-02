package com.ilieinc.dontsleep.viewmodel.base

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.ilieinc.core.viewmodel.base.DialogViewModel

abstract class HelpDialogViewModel(
    onDismissRequestedCallback: () -> Unit,
    application: Application
) : DialogViewModel(onDismissRequestedCallback, application) {
    var showRevokePermissionButton by mutableStateOf(false)
        protected set
    var revokeButtonText by mutableStateOf("Revoke Permission")
        protected set

    open fun revokePermission() {}
}