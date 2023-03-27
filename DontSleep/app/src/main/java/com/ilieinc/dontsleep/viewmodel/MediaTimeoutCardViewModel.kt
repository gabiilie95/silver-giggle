package com.ilieinc.dontsleep.viewmodel

import android.app.Application
import com.ilieinc.dontsleep.service.MediaTimeoutService
import com.ilieinc.dontsleep.service.ScreenTimeoutService
import com.ilieinc.dontsleep.util.DeviceAdminHelper
import com.ilieinc.dontsleep.util.StateHelper.startForegroundService
import com.ilieinc.dontsleep.util.StateHelper.stopService
import com.ilieinc.dontsleep.viewmodel.base.CardViewModel

class MediaTimeoutCardViewModel(application: Application) :
    CardViewModel(application, MediaTimeoutService.serviceRunning) {
    override val tag: String = MediaTimeoutService.MEDIA_TIMEOUT_TAG

    init {
        title.tryEmit("Media Timeout")
        setSavedTime()
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