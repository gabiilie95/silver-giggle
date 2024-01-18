package com.ilieinc.dontsleep.viewmodel

import android.app.Application
import android.content.Intent
import androidx.core.content.ContextCompat
import com.ilieinc.dontsleep.service.WakeLockService
import com.ilieinc.core.util.PermissionHelper
import com.ilieinc.core.util.StateHelper.startForegroundService
import com.ilieinc.core.util.StateHelper.stopService
import com.ilieinc.dontsleep.R
import com.ilieinc.dontsleep.viewmodel.base.CardViewModel
import kotlinx.coroutines.flow.update

class WakeLockCardViewModel(application: Application) :
    CardViewModel(application, WakeLockService.serviceRunning) {
    override val tag: String = WakeLockService.TIMEOUT_TAG
    override val showTimeoutSectionToggle = true
    override val timeoutEnabledTag = WakeLockService.TIMEOUT_ENABLED_TAG

    init {
        updateTitle(context.getString(R.string.don_t_sleep))
        setSavedTime()
        setSavedTimeoutStatus()
        refreshPermissionState()
    }

    override fun refreshPermissionState() {
        uiModel.update {
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