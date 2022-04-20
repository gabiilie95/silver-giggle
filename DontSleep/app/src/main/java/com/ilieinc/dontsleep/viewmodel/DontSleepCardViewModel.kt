package com.ilieinc.dontsleep.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.ilieinc.dontsleep.service.TimeoutService
import com.ilieinc.dontsleep.util.PermissionHelper
import com.ilieinc.dontsleep.util.StateHelper.startForegroundService
import com.ilieinc.dontsleep.util.StateHelper.stopService
import com.ilieinc.dontsleep.viewmodel.base.CardViewModel
import kotlinx.coroutines.launch

class DontSleepCardViewModel(application: Application): CardViewModel(
    application,
    TimeoutService.serviceRunning
) {
    override val tag: String = TimeoutService.TIMEOUT_TAG

    init {
        title.tryEmit("Don't Sleep!")
        permissionRequired.tryEmit(PermissionHelper.shouldRequestDrawOverPermission(getApplication()))
        setSavedTime()
    }

    override fun startService() {
        getApplication<Application>().startForegroundService<TimeoutService>()
    }

    override fun stopService() {
        getApplication<Application>().stopService<TimeoutService>()
    }
}