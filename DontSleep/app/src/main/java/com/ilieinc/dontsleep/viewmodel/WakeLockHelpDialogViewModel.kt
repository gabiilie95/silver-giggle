package com.ilieinc.dontsleep.viewmodel

import android.app.Application
import com.ilieinc.core.util.PermissionHelper
import com.ilieinc.dontsleep.R
import com.ilieinc.dontsleep.viewmodel.base.HelpDialogViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class WakeLockHelpDialogViewModel @Inject constructor(
    application: Application
) : HelpDialogViewModel(application) {
    init {
        setDetails()
    }

    private fun setDetails() {
        state.update {
            it.copy(
                title = context.getString(R.string.wakelock_help_title),
                description = buildString {
                    append(context.getString(R.string.wakelock_help_description))
                    if (PermissionHelper.hasDrawOverPermission(getApplication())) {
                        append(context.getString(R.string.revoke_permission_description))
                    }
                },
                revokeButtonText = context.getString(R.string.revoke_draw_over_permission),
                showRevokePermissionButton = PermissionHelper.hasDrawOverPermission(getApplication())
            )
        }

    }

    override fun revokePermission() {
        PermissionHelper.requestDrawOverPermission(getApplication())
        onDismissRequested()
    }
}
