package com.ilieinc.dontsleep.viewmodel

import android.app.Application
import com.ilieinc.dontsleep.service.WakeLockService
import com.ilieinc.dontsleep.util.PermissionHelper
import com.ilieinc.dontsleep.util.StateHelper.startForegroundService
import com.ilieinc.dontsleep.util.StateHelper.stopService
import com.ilieinc.dontsleep.viewmodel.base.CardViewModel

class WakeLockCardViewModel(application: Application): CardViewModel(
    application,
    WakeLockService.serviceRunning
) {
    override val tag: String = WakeLockService.TIMEOUT_TAG

    init {
        title.tryEmit("Don't Sleep!")
        refreshPermissionState()
        setSavedTime()
    }

    override fun refreshPermissionState() {
        permissionRequired.tryEmit(PermissionHelper.shouldRequestDrawOverPermission(getApplication()))
    }

    override fun startService() {
        getApplication<Application>().startForegroundService<WakeLockService>()
    }

    override fun stopService() {
        getApplication<Application>().stopService<WakeLockService>()
    }
}