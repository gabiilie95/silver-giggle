package com.ilieinc.dontsleep.viewmodel

import android.app.Application
import com.ilieinc.dontsleep.service.ScreenTimeoutService
import com.ilieinc.dontsleep.util.DeviceAdminHelper
import com.ilieinc.dontsleep.util.StateHelper.startForegroundService
import com.ilieinc.dontsleep.util.StateHelper.stopService
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