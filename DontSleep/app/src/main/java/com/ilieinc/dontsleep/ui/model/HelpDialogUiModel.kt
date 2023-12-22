package com.ilieinc.dontsleep.ui.model

data class HelpDialogUiModel(
    val title: String = "",
    val description: String = "",
    val showRevokePermissionButton: Boolean = false,
    val revokeButtonText: String = "Revoke Permission"
)