package com.ilieinc.dontsleep.viewmodel

import android.app.Application
import com.ilieinc.dontsleep.util.PermissionHelper
import com.ilieinc.dontsleep.viewmodel.base.PermissionDialogViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class WakeLockPermissionDialogViewModel(
    showDialog: MutableStateFlow<Boolean>,
    application: Application
) : PermissionDialogViewModel(showDialog, application) {

    init {
        title.tryEmit("Draw Over Permission Grant")
        description.tryEmit(
            "Due to limitations from the manufacturer of your device, in order to use this feature, you must grant draw over other apps permission to the application.\n" +
                    "This permission will only be used to keep the screen awake.\n\n" +
                    "Do you want to continue?"
        )
    }

    override fun requestPermission() {
        PermissionHelper.requestDrawOverPermission(getApplication())
        showDialog.tryEmit(false)
    }
}
