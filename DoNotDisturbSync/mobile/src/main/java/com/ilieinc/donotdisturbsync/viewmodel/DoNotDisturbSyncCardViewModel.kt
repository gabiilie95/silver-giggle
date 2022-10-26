package com.ilieinc.donotdisturbsync.viewmodel

import android.app.Application
import androidx.core.content.edit
import androidx.lifecycle.viewModelScope
import com.ilieinc.donotdisturbsync.util.PermissionHelper
import com.ilieinc.donotdisturbsync.viewmodel.base.CardViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class DoNotDisturbSyncCardViewModel(application: Application): CardViewModel(application) {
    companion object {
        const val phoneToWearableTag = "PhoneToWearableSyncEnabled"
        const val wearableToPhoneTag = "WearableToPhoneSyncEnabled"
    }

    val notificationUtils = com.ilieinc.common.util.NotificationUtils(application)

    var phoneToWearableSyncEnabled = MutableStateFlow(false)
    var wearableToPhoneSyncEnabled = MutableStateFlow(false)

    init {
        title.tryEmit("Sync Status")
        loadInitialState()
        refreshPermissionState()
        viewModelScope.launch {
//            settingEnabled.collect { serviceRunning ->
//                enabled.tryEmit(serviceRunning)
//            }
        }
        viewModelScope.launch {
            phoneToWearableSyncEnabled.collect { enabled ->
                cardStatusChanged(enabled, phoneToWearableTag)
            }
        }
        viewModelScope.launch {
            wearableToPhoneSyncEnabled.collect { enabled ->
                cardStatusChanged(enabled, wearableToPhoneTag)
            }
        }
    }

    private fun loadInitialState() {
        with(com.ilieinc.common.util.SharedPreferenceManager.getInstance(getApplication())) {
            phoneToWearableSyncEnabled.tryEmit(getBoolean(phoneToWearableTag, false))
            wearableToPhoneSyncEnabled.tryEmit(getBoolean(wearableToPhoneTag, false))
        }
    }

    override fun refreshPermissionState() {
        permissionRequired.tryEmit(!PermissionHelper.hasNotificationPolicyAccessPermission(getApplication()))
    }

    private fun cardStatusChanged(enabled: Boolean, tag: String) {
        com.ilieinc.common.util.SharedPreferenceManager.getInstance(getApplication()).edit(true) {
            putBoolean(tag, enabled)
            notificationUtils.changeDoNotDisturbStatus(enabled)
        }
    }
}