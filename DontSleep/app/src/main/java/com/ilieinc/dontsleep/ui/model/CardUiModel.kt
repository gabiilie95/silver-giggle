package com.ilieinc.dontsleep.ui.model

data class CardUiModel(
    val title: String = "",
    val hours: Int = 0,
    val minutes: Int = 0,
    val timeoutEnabled: Boolean = true,
    val enabled: Boolean = false,
    val showTimeoutSectionToggle: Boolean = true,
    val timeoutSectionToggleEnabled: Boolean = true,
    val showPermissionDialog: Boolean = false,
    val showHelpDialog: Boolean = false,
    val permissionRequired: Boolean = false
)