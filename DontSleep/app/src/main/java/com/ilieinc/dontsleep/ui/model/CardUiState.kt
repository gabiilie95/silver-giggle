package com.ilieinc.dontsleep.ui.model

data class CardUiState(
    val title: String = "",
    val hours: Int? = null,
    val minutes: Int? = null,
    val timeoutEnabled: Boolean = true,
    val enabled: Boolean = false,
    val showTimeoutSectionToggle: Boolean = true,
    val timeoutSectionToggleEnabled: Boolean = true,
    val showPermissionDialog: Boolean = false,
    val showHelpDialog: Boolean = false,
    val permissionRequired: Boolean = false
)