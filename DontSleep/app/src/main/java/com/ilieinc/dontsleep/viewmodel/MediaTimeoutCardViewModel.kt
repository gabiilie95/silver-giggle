package com.ilieinc.dontsleep.viewmodel

import android.app.Application
import com.ilieinc.dontsleep.service.MediaTimeoutService
import com.ilieinc.core.util.StateHelper.startForegroundService
import com.ilieinc.core.util.StateHelper.stopService
import com.ilieinc.dontsleep.R
import com.ilieinc.dontsleep.viewmodel.base.CardViewModel
import kotlinx.coroutines.flow.update

class MediaTimeoutCardViewModel(application: Application) :
    CardViewModel(application, MediaTimeoutService.serviceRunning) {
    override val tag: String = MediaTimeoutService.MEDIA_TIMEOUT_TAG
    override val showTimeoutSectionToggle = false
    override val timeoutEnabledTag = MediaTimeoutService.TIMEOUT_ENABLED_TAG

    init {
        updateTitle(context.getString(R.string.media_timeout_title))
        setAutoOffToggleDisabled()
        setSavedTime()
        setSavedTimeoutStatus()
    }

    private fun setAutoOffToggleDisabled() {
        uiModel.update { it.copy(showTimeoutSectionToggle = false) }
    }

    override fun refreshPermissionState() {
    }

    override fun startService() {
        getApplication<Application>().startForegroundService<MediaTimeoutService>()
    }

    override fun stopService() {
        getApplication<Application>().stopService<MediaTimeoutService>()
    }
}