package com.ilieinc.dontsleep.viewmodel

import android.app.Application
import com.ilieinc.core.util.DeviceAdminHelper
import com.ilieinc.dontsleep.viewmodel.base.HelpDialogViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class MediaTimeoutCardHelpDialogViewModel(
    showDialog: MutableStateFlow<Boolean>,
    application: Application,
) :
    HelpDialogViewModel(showDialog, application) {
    init {
        title.tryEmit("Media Timeout Help")
        setDetails()
    }

    private fun setDetails() {
        description.tryEmit(buildString {
            append("This feature allows you to set a timer which will pause any media playing on the device, such as music or video playback.")
        })
    }
}
