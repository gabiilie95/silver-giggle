package com.ilieinc.dontsleep.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.ilieinc.dontsleep.service.MediaTimeoutService
import com.ilieinc.core.util.StateHelper.startForegroundService
import com.ilieinc.core.util.StateHelper.stopService
import com.ilieinc.dontsleep.R
import com.ilieinc.dontsleep.data.DontSleepDataStore
import com.ilieinc.dontsleep.viewmodel.base.CardViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MediaTimeoutCardViewModel(application: Application) : CardViewModel(
    application = application,
    serviceClass = MediaTimeoutService::class.java,
    serviceRunning = MediaTimeoutService.serviceRunning
) {
    override val tag: String = MediaTimeoutService.MEDIA_TIMEOUT_TAG
    override val showTimeoutSectionToggle = false
    override val timeoutPreferenceKey = DontSleepDataStore.MEDIA_TIMEOUT_PREF_KEY
    override val timeoutEnabledPreferenceKey = DontSleepDataStore.MEDIA_TIMEOUT_ENABLED_PREF_KEY

    init {
        updateTitle(context.getString(R.string.media_timeout_title))
        setAutoOffToggleDisabled()
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                setSavedTime()
                setSavedTimeoutStatus()
            }
        }
    }

    private fun setAutoOffToggleDisabled() {
        _state.update { it.copy(showTimeoutSectionToggle = false) }
    }
}