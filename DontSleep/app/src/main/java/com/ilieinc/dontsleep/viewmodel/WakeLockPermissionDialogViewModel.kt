package com.ilieinc.dontsleep.viewmodel

import android.app.Application
import com.ilieinc.core.util.PermissionHelper
import com.ilieinc.core.viewmodel.PermissionDialogViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class WakeLockPermissionDialogViewModel(
    onDismissRequestedCallback: () -> Unit,
    application: Application
) : PermissionDialogViewModel(onDismissRequestedCallback, application) {

    init {
        title = "Draw Over Permission Grant"
        description = 
            "Due to limitations from the manufacturer of your device, in order to use this feature, you must grant draw over other apps permission to the application.\n" +
                    "This permission will only be used to keep the screen awake.\n\n" +
                    "Do you want to continue?"
    }

    override fun requestPermission() {
        PermissionHelper.requestDrawOverPermission(getApplication())
        onDismissRequested()
    }
}
