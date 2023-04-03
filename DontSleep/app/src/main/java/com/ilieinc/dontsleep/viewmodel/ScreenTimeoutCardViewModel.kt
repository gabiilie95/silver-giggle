package com.ilieinc.dontsleep.viewmodel

import android.app.Application
import com.ilieinc.dontsleep.service.ScreenTimeoutService
import com.ilieinc.core.util.DeviceAdminHelper
import com.ilieinc.core.util.StateHelper.startForegroundService
import com.ilieinc.core.util.StateHelper.stopService
import com.ilieinc.dontsleep.viewmodel.base.CardViewModel

class ScreenTimeoutCardViewModel(application: Application) :
    CardViewModel(application, ScreenTimeoutService.serviceRunning) {
    override val tag: String = ScreenTimeoutService.SLEEP_TAG

    init {
        title.tryEmit("Sleep!")
        refreshPermissionState()
        setSavedTime()
    }

    override fun refreshPermissionState() {
        permissionRequired.tryEmit(!DeviceAdminHelper.adminPermissionGranted())
    }

    override fun startService() {
        getApplication<Application>().startForegroundService<ScreenTimeoutService>()
    }

    override fun stopService() {
        getApplication<Application>().stopService<ScreenTimeoutService>()
    }
}