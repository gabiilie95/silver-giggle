package com.ilieinc.dontsleep.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.ilieinc.dontsleep.service.SleepService
import com.ilieinc.dontsleep.service.TimeoutService
import com.ilieinc.dontsleep.util.DeviceAdminHelper
import com.ilieinc.dontsleep.util.StateHelper.startForegroundService
import com.ilieinc.dontsleep.util.StateHelper.stopService
import com.ilieinc.dontsleep.viewmodel.base.CardViewModel
import kotlinx.coroutines.launch

class SleepCardViewModel(application: Application) :
    CardViewModel(application, SleepService.serviceRunning) {
    override val tag: String = SleepService.SLEEP_TAG

    init {
        title.tryEmit("Sleep!")
        refreshPermissionState()
        setSavedTime()
    }

    override fun refreshPermissionState() {
        permissionRequired.tryEmit(!DeviceAdminHelper.adminPermissionGranted())
    }

    override fun startService() {
        getApplication<Application>().startForegroundService<SleepService>()
    }

    override fun stopService() {
        getApplication<Application>().stopService<SleepService>()
    }
}