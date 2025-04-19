package com.ilieinc.core.viewmodel.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

abstract class DialogViewModel(
    application: Application
) : AndroidViewModel(application) {
    protected val context get() = getApplication<Application>()

    private val _onDismissSharedFlow = MutableSharedFlow<Unit?>()
    val onDismissSharedFlow = _onDismissSharedFlow.asSharedFlow()

    open fun onDismissRequested() {
        viewModelScope.launch {
            _onDismissSharedFlow.emit(Unit)
        }
    }
}
