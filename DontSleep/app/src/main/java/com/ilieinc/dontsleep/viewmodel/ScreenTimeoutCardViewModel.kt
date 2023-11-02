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
    override val showTimeoutSectionToggle = true
    override val timeoutEnabledTag = ScreenTimeoutService.TIMEOUT_ENABLED_TAG

    init {
        title = "Sleep!"
        setSavedTime()
        setSavedTimeoutStatus()
        refreshPermissionState()
    }

    override fun refreshPermissionState() {
        permissionRequired = !DeviceAdminHelper.adminPermissionGranted()
    }

    override fun startService() {
        getApplication<Application>().startForegroundService<ScreenTimeoutService>()
    }

    override fun stopService() {
        getApplication<Application>().stopService<ScreenTimeoutService>()
    }

    fun updatePermissionRequired(permissionRequired: Boolean) {
        this.permissionRequired = permissionRequired
    }
}