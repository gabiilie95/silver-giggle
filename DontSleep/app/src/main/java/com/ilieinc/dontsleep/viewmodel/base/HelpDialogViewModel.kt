package com.ilieinc.dontsleep.viewmodel.base

import android.app.Application
import com.ilieinc.core.viewmodel.base.DialogViewModel
import com.ilieinc.dontsleep.DontSleepApplication
import com.ilieinc.dontsleep.ui.model.HelpDialogUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

abstract class HelpDialogViewModel(application: Application) : DialogViewModel(application) {
    val state = MutableStateFlow(HelpDialogUiState())

    open fun revokePermission() {}
}