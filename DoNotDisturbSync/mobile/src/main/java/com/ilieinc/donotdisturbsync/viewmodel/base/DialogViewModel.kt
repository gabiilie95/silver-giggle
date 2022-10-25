package com.ilieinc.donotdisturbsync.viewmodel.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow

abstract class DialogViewModel(
    val showDialog: MutableStateFlow<Boolean>,
    application: Application
) : AndroidViewModel(application) {
    val title = MutableStateFlow("")
    val description = MutableStateFlow("")
}
