package com.ilieinc.dontsleep.ui.model

data class HelpDialogUiState(
    val title: String = "",
    val description: String = "",
    val showRevokePermissionButton: Boolean = false,
    val revokeButtonText: String = "Revoke Permission"
)