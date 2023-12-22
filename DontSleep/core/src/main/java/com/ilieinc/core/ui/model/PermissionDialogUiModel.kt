package com.ilieinc.core.ui.model

data class PermissionDialogUiModel(
    val title: String = "",
    val description: String = "",
    var confirmButtonEnabled: Boolean = true,
    var confirmButtonText: String = "Yes"
)