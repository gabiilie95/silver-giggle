package com.ilieinc.dontsleep.viewmodel

import android.app.Application
import com.ilieinc.core.util.DeviceAdminHelper
import com.ilieinc.dontsleep.R
import com.ilieinc.dontsleep.viewmodel.base.HelpDialogViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class MediaTimeoutCardHelpDialogViewModel(
    onDismissRequestedCallback: () -> Unit,
    application: Application,
) : HelpDialogViewModel(onDismissRequestedCallback, application) {
    init {
        title = "Media Timeout Help"
        setDetails()
    }

    private fun setDetails() {
        description = buildString {
            append(getApplication<Application>().getString(R.string.media_timeout_description))
        }
    }
}
