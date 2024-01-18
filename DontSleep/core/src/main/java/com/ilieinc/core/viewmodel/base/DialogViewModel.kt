package com.ilieinc.core.viewmodel.base

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow

abstract class DialogViewModel(
    private val onDismissRequestedCallback: () -> Unit,
    application: Application
) : AndroidViewModel(application) {
    protected val context get() = getApplication<Application>()

    open fun onDismissRequested() {
        onDismissRequestedCallback()
    }
}
