package com.ilieinc.dontsleep.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.ilieinc.core.util.PermissionHelper
import com.ilieinc.dontsleep.R
import com.ilieinc.dontsleep.data.DontSleepDataStore
import com.ilieinc.dontsleep.service.MediaTimeoutService
import com.ilieinc.dontsleep.viewmodel.base.CardViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MediaTimeoutCardViewModel @Inject constructor(application: Application) : CardViewModel(
    application = application,
    serviceClass = MediaTimeoutService::class.java,
    serviceRunning = MediaTimeoutService.serviceRunning
) {
    override val tag: String = MediaTimeoutService.MEDIA_TIMEOUT_TAG
    override val showTimeoutSectionToggle = false
    override val statePreferenceKey = DontSleepDataStore.MEDIA_STATE_PREF_KEY

    init {
        updateTitle(context.getString(R.string.media_timeout_title))
        setAutoOffToggleDisabled()
    }

    private fun setAutoOffToggleDisabled() {
        _state.update { it.copy(showTimeoutSectionToggle = false) }
    }
}