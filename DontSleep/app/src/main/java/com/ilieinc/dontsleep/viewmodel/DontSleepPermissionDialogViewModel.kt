package com.ilieinc.dontsleep.viewmodel

import android.app.Application
import com.ilieinc.dontsleep.util.PermissionHelper
import com.ilieinc.dontsleep.viewmodel.base.PermissionDialogViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class DontSleepPermissionDialogViewModel(
    showDialog: MutableStateFlow<Boolean>,
    application: Application
) : PermissionDialogViewModel(showDialog, application) {

    override fun requestPermission() {
        PermissionHelper.requestDrawOverPermission(getApplication())
    }
}
