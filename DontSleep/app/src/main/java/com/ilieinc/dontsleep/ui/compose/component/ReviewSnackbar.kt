package com.ilieinc.dontsleep.ui.compose.component

import android.content.Intent
import android.net.Uri
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.edit
import com.ilieinc.dontsleep.util.SharedPreferenceManager
import com.ilieinc.dontsleep.util.StateHelper

@Composable
fun ReviewSnackbar() {
    val context = LocalContext.current
    val sharedPreferenceManager = remember { SharedPreferenceManager.getInstance(context) }
    var snackbarWasShown by remember {
        mutableStateOf(
            sharedPreferenceManager.getBoolean(
                StateHelper.RATING_SHOWN,
                false
            )
        )
    }
    if (!snackbarWasShown) {
        with(sharedPreferenceManager) {
            val startNum = getInt(StateHelper.APP_START_COUNT, 0)
            if (true || startNum != 0 && startNum % 5 == 0) {
                Snackbar(
                    action = {
                        TextButton(onClick = {
                            context.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("market://details?id=${context.packageName}")
                                )
                            )
                            edit(true) { putBoolean(StateHelper.RATING_SHOWN, true) }
                        }) {
                            Text("RATE APP")
                        }
                    }
                ) {
                    Text(text = "Please rate the app if you are enjoying it :)")
                }
                snackbarWasShown = true
            }
        }
    }
}