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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class WakeLockCardViewModel @Inject constructor(application: Application) : CardViewModel(
    application = application,
    serviceClass = WakeLockService::class.java,
    serviceRunning = WakeLockService.serviceRunning
) {
    override val tag: String = WakeLockService.TIMEOUT_TAG
    override val showTimeoutSectionToggle = true
    override val statePreferenceKey = DontSleepDataStore.WAKE_LOCK_STATE_PREF_KEY

    override fun onDataLoaded() {
        updateTitle(context.getString(R.string.don_t_sleep))
    }

    override fun refreshPermissionState() {
        _state.update {
            it.copy(
                permissionRequired = PermissionHelper.shouldRequestDrawOverPermission(context)
            )
        }
    }
}