package com.ilieinc.dontsleep.viewmodel.base

import android.app.Application
import com.ilieinc.core.viewmodel.base.DialogViewModel
import com.ilieinc.dontsleep.ui.model.HelpDialogUiModel
import kotlinx.coroutines.flow.MutableStateFlow

abstract class HelpDialogViewModel(
    onDismissRequestedCallback: () -> Unit,
    application: Application
) : DialogViewModel(onDismissRequestedCallback, application) {
    val uiModel = MutableStateFlow(HelpDialogUiModel())

    open fun revokePermission() {}
}