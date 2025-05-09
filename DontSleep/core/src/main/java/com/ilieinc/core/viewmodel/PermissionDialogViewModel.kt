package com.ilieinc.core.viewmodel

import android.app.Application
import com.ilieinc.core.ui.model.PermissionDialogUiModel
import com.ilieinc.core.viewmodel.base.DialogViewModel
import kotlinx.coroutines.flow.MutableStateFlow

abstract class PermissionDialogViewModel(
    application: Application
) : DialogViewModel(application) {
    val state = MutableStateFlow(PermissionDialogUiModel())

    abstract fun requestPermission()
}
