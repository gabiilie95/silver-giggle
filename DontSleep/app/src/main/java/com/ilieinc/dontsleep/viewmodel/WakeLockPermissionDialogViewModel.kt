package com.ilieinc.dontsleep.viewmodel

import android.app.Application
import com.ilieinc.core.util.PermissionHelper
import com.ilieinc.core.viewmodel.PermissionDialogViewModel
import com.ilieinc.dontsleep.R
import kotlinx.coroutines.flow.update

class WakeLockPermissionDialogViewModel(
    onDismissRequestedCallback: () -> Unit,
    application: Application
) : PermissionDialogViewModel(onDismissRequestedCallback, application) {

    init {
        setTitleAndDescription()
    }

    private fun setTitleAndDescription() {
        state.update {
            it.copy(
                title = context.getString(R.string.draw_over_permission_title),
                description = context.getString(R.string.draw_over_permission_description)
            )
        }
    }

    override fun requestPermission() {
        PermissionHelper.requestDrawOverPermission(getApplication())
        onDismissRequested()
    }
}
