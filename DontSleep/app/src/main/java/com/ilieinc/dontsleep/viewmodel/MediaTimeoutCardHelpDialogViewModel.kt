package com.ilieinc.dontsleep.viewmodel

import android.app.Application
import com.ilieinc.dontsleep.R
import com.ilieinc.dontsleep.viewmodel.base.HelpDialogViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MediaTimeoutCardHelpDialogViewModel @Inject constructor(
    application: Application,
) : HelpDialogViewModel(application) {
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
