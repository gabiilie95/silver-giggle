package com.ilieinc.dontsleep.viewmodel

import android.app.Activity
import androidx.lifecycle.viewModelScope
import com.ilieinc.core.util.DeviceAdminHelper
import com.ilieinc.dontsleep.viewmodel.base.PermissionDialogViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ScreenTimeoutPermissionDialogViewModel(
    showDialog: MutableStateFlow<Boolean>,
    private val activity: Activity
) : PermissionDialogViewModel(showDialog, activity.application) {
    private var warningIsShowing = false

    init {
        setFieldValues()
    }

    private fun setFieldValues() {
        if (!warningIsShowing) {
            title.tryEmit("Special Permission Grant")
            description.tryEmit(
                "In order to use this feature, you must grant a special permission to the application.\n" +
                        "This permission allows the application to turn off your screen.\n\n" +
                        "Do you want to continue?"
            )
        } else {
            title.tryEmit("WARNING")
            description.tryEmit(
                "You will not be able to uninstall the application until you revoke the administrator permission.\n" +
                        "You can revoke the permission by clicking the 'Help' button from the 'Sleep! Timer' section of the app.\n" +
                        "Do you want to continue?"
            )
            viewModelScope.launch { startTimeoutForConfirmation() }
        }
    }

    private suspend fun startTimeoutForConfirmation() {
        confirmButtonEnabled.tryEmit(false)
        var timeout = 10
        while (timeout > 0){
            confirmButtonText.tryEmit("Yes ($timeout)")
            delay(1000)
            timeout -= 1
        }
        confirmButtonText.tryEmit("Yes")
        confirmButtonEnabled.tryEmit(true)
    }

    override fun requestPermission() {
        if (!warningIsShowing) {
            warningIsShowing = true
            setFieldValues()
        } else {
            DeviceAdminHelper.requestAdminPermission(activity)
            showDialog.tryEmit(false)
        }
    }
}
