package com.ilieinc.donotdisturbsync.presentation.theme

import androidx.compose.runtime.Composable
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.MaterialTheme

internal val wearColorPalette: Colors = Colors(
	primary = md_theme_dark_primary,
	primaryVariant = md_theme_dark_primaryContainer,
	secondary = md_theme_dark_secondary,
	secondaryVariant = md_theme_dark_onSecondary,
	error = md_theme_dark_error,
	onPrimary = md_theme_dark_onPrimary,
	onSecondary = md_theme_dark_onSecondary,
	onError = md_theme_dark_onError
)

@Composable
fun DoNotDisturbSyncTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = wearColorPalette,
        typography = Typography,
        // For shapes, we generally recommend using the default Material Wear shapes which are
        // optimized for round and non-round devices.
        content = content
    )
}