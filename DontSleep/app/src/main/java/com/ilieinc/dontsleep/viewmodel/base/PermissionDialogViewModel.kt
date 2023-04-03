package com.ilieinc.dontsleep.viewmodel.base

import android.app.Application
import com.ilieinc.core.viewmodel.base.DialogViewModel
import kotlinx.coroutines.flow.MutableStateFlow

abstract class PermissionDialogViewModel(
    showDialog: MutableStateFlow<Boolean>,
    application: Application
) : DialogViewModel(showDialog, application) {
    val confirmButtonEnabled = MutableStateFlow(true)
    val confirmButtonText = MutableStateFlow("Yes")

    abstract fun requestPermission()
}
