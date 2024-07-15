package com.ilieinc.dontsleep.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.ilieinc.dontsleep.service.WakeLockService
import com.ilieinc.core.util.PermissionHelper
import com.ilieinc.core.util.StateHelper.startForegroundService
import com.ilieinc.core.util.StateHelper.stopService
import com.ilieinc.dontsleep.R
import com.ilieinc.dontsleep.data.DontSleepDataStore
import com.ilieinc.dontsleep.viewmodel.base.CardViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WakeLockCardViewModel(application: Application) : CardViewModel(
    application = application,
    serviceClass = WakeLockService::class.java,
    serviceRunning = WakeLockService.serviceRunning
) {
    override val tag: String = WakeLockService.TIMEOUT_TAG
    override val showTimeoutSectionToggle = true
    override val timeoutPreferenceKey = DontSleepDataStore.WAKE_LOCK_TIMEOUT_PREF_KEY
    override val timeoutEnabledPreferenceKey = DontSleepDataStore.WAKE_LOCK_TIMEOUT_ENABLED_PREF_KEY

    init {
        updateTitle(context.getString(R.string.don_t_sleep))
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                setSavedTime()
                setSavedTimeoutStatus()
            }
            refreshPermissionState()
        }
    }

    override fun refreshPermissionState() {
        _state.update {
            it.copy(
                permissionRequired = PermissionHelper.shouldRequestDrawOverPermission(context)
            )
        }
    }

    override fun startService() {
        context.startForegroundService<WakeLockService>()
    }

    override fun stopService() {
        context.stopService<WakeLockService>()
    }
}