package com.ilieinc.dontsleep.viewmodel

import android.app.Application
import com.ilieinc.dontsleep.R
import com.ilieinc.dontsleep.viewmodel.base.HelpDialogViewModel
import kotlinx.coroutines.flow.update

class MediaTimeoutCardHelpDialogViewModel(
    onDismissRequestedCallback: () -> Unit,
    application: Application,
) : HelpDialogViewModel(onDismissRequestedCallback, application) {
    init {
        setDetails()
    }

    private fun setDetails() {
        state.update {
            it.copy(
                title = context.getString(R.string.media_timeout_help_title),
                description = buildString {
                    append(context.getString(R.string.media_timeout_help_description))
                }
            )
        }
    }
}
