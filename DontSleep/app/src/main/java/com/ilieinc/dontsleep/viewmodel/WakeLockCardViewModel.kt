package com.ilieinc.dontsleep.viewmodel

import android.app.Application
import com.ilieinc.dontsleep.service.WakeLockService
import com.ilieinc.core.util.PermissionHelper
import com.ilieinc.core.util.StateHelper.startForegroundService
import com.ilieinc.core.util.StateHelper.stopService
import com.ilieinc.dontsleep.viewmodel.base.CardViewModel

class WakeLockCardViewModel(application: Application): CardViewModel(
    application,
    WakeLockService.serviceRunning
) {
    override val tag: String = WakeLockService.TIMEOUT_TAG
    override val showTimeoutSectionToggle = true
    override val timeoutEnabledTag = WakeLockService.TIMEOUT_ENABLED_TAG

    init {
        title = "Don't Sleep!"
        setSavedTime()
        setSavedTimeoutStatus()
        refreshPermissionState()
    }

    override fun refreshPermissionState() {
        permissionRequired = PermissionHelper.shouldRequestDrawOverPermission(getApplication())
    }

    override fun startService() {
        getApplication<Application>().startForegroundService<WakeLockService>()
    }

    override fun stopService() {
        getApplication<Application>().stopService<WakeLockService>()
    }
}