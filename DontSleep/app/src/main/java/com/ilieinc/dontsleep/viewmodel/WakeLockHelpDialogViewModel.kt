package com.ilieinc.dontsleep.viewmodel

import android.app.Application
import com.ilieinc.core.util.PermissionHelper
import com.ilieinc.dontsleep.R
import com.ilieinc.dontsleep.viewmodel.base.HelpDialogViewModel
import kotlinx.coroutines.flow.update

class WakeLockHelpDialogViewModel(
    onDismissRequestedCallback: () -> Unit,
    application: Application
) : HelpDialogViewModel(onDismissRequestedCallback, application) {
    init {
        setDetails()
    }

    private fun setDetails() {
        uiModel.update {
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
