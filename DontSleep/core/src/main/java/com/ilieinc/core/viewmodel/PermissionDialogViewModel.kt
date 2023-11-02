package com.ilieinc.core.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.ilieinc.core.viewmodel.base.DialogViewModel
import kotlinx.coroutines.flow.MutableStateFlow

abstract class PermissionDialogViewModel(
    onDismissRequestedCallback: () -> Unit,
    application: Application
) : DialogViewModel(onDismissRequestedCallback, application) {
    var confirmButtonEnabled by mutableStateOf(true)
        protected set
    var confirmButtonText by mutableStateOf("Yes")
        protected set

    abstract fun requestPermission()
}
