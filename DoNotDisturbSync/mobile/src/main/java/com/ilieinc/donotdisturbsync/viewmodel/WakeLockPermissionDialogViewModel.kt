package com.ilieinc.donotdisturbsync.viewmodel

import android.app.Application
import com.ilieinc.donotdisturbsync.util.PermissionHelper
import com.ilieinc.donotdisturbsync.viewmodel.base.PermissionDialogViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class WakeLockPermissionDialogViewModel(
    showDialog: MutableStateFlow<Boolean>,
    application: Application
) : PermissionDialogViewModel(showDialog, application) {

    init {
        title.tryEmit("Do Not Disturb Access Permission Grant")
        description.tryEmit(
            "Due to limitations from the manufacturer of your device, in order to use this feature, you must grant draw over other apps permission to the application.\n" +
                    "This permission will only be used to keep the screen awake.\n\n" +
                    "Do you want to continue?"
        )
    }

    override fun requestPermission() {
        PermissionHelper.requestNotificationPolicyAccessPermission(getApplication())
        showDialog.tryEmit(false)
    }
}
