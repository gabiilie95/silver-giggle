package com.ilieinc.donotdisturbsync.viewmodel.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow

abstract class CardViewModel(application: Application) :
    AndroidViewModel(application) {
    val showPermissionDialog = MutableStateFlow(false)

    val title = MutableStateFlow("")
    val showHelpDialog = MutableStateFlow(false)
    val permissionRequired = MutableStateFlow(false)

    abstract fun refreshPermissionState()
}