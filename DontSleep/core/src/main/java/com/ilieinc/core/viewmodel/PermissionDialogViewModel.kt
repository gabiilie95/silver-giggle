package com.ilieinc.core.viewmodel

import android.app.Application
import com.ilieinc.core.ui.model.PermissionDialogUiModel
import com.ilieinc.core.viewmodel.base.DialogViewModel
import kotlinx.coroutines.flow.MutableStateFlow

abstract class PermissionDialogViewModel(
    onDismissRequestedCallback: () -> Unit,
    application: Application
) : DialogViewModel(onDismissRequestedCallback, application) {
    val uiModel = MutableStateFlow(PermissionDialogUiModel())

    abstract fun requestPermission()
}
