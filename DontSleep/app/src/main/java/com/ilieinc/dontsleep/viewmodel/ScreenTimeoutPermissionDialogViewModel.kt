package com.ilieinc.dontsleep.viewmodel

import android.app.Activity
import android.app.Application
import androidx.lifecycle.viewModelScope
import com.ilieinc.core.util.DeviceAdminHelper
import com.ilieinc.core.viewmodel.PermissionDialogViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ScreenTimeoutPermissionDialogViewModel(
    onDismissRequestedCallback: () -> Unit,
    private val activity: Activity,
) : PermissionDialogViewModel(onDismissRequestedCallback, activity.application) {
    private var warningIsShowing = false

    init {
        setFieldValues()
    }

    private fun setFieldValues() {
        if (!warningIsShowing) {
            title = "Special Permission Grant"
            description = 
                "In order to use this feature, you must grant a special permission to the application.\n" +
                        "This permission allows the application to turn off your screen.\n\n" +
                        "Do you want to continue?"
        } else {
            title = "WARNING"
            description = 
                "You will not be able to uninstall the application until you revoke the administrator permission.\n" +
                        "You can revoke the permission by clicking the 'Help' button from the 'Sleep! Timer' section of the app.\n" +
                        "Do you want to continue?"
            viewModelScope.launch { startTimeoutForConfirmation() }
        }
    }

    private suspend fun startTimeoutForConfirmation() {
        confirmButtonEnabled = false
        var timeout = 10
        while (timeout > 0){
            confirmButtonText = "Yes ($timeout)"
            delay(1000)
            timeout -= 1
        }
        confirmButtonText = "Yes"
        confirmButtonEnabled = true
    }

    override fun requestPermission() {
        if (!warningIsShowing) {
            warningIsShowing = true
            setFieldValues()
        } else {
            DeviceAdminHelper.requestAdminPermission(activity)
            onDismissRequested()
        }
    }
}
