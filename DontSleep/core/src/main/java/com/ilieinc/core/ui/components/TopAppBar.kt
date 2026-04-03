package com.ilieinc.core.ui.components

import android.os.Build
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.outlined.InvertColors
import androidx.compose.material.icons.outlined.InvertColorsOff
import androidx.compose.material.icons.outlined.StarRate
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.ilieinc.core.R
import com.ilieinc.core.util.StateHelper
import com.ilieinc.core.viewmodel.RatingDialogViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationTopAppBar() {
    val context = LocalContext.current
    val useDynamicColors by StateHelper.useDynamicColors.collectAsState()
    var showRateDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Smartphone,
                    "Title Icon"
                )
                Text(
                    modifier = Modifier.padding(start = 5.dp),
                    text = stringResource(R.string.app_name),
                    fontWeight = FontWeight.Bold
                )
            }
        },
        actions = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                IconButton(onClick = {
                    scope.launch {
                        StateHelper.setDynamicColorsEnabled(
                            context = context,
                            enabled = !useDynamicColors
                        )
                    }
                }) {
                    Icon(
                        if (useDynamicColors) {
                            Icons.Outlined.InvertColors
                        } else {
                            Icons.Outlined.InvertColorsOff
                        },
                        "Dynamic Colors Image"
                    )
                }
            }
            IconButton(onClick = { showRateDialog = true }) {
                Icon(Icons.Outlined.StarRate, "Rating Image")
            }
        })
    if (showRateDialog) {
        RatingDialog(
            viewModel = hiltViewModel<RatingDialogViewModel>(),
            onDismiss = { showRateDialog = false },
        )
    }
}