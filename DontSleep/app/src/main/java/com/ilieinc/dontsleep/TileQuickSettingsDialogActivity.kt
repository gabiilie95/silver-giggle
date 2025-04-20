package com.ilieinc.dontsleep

import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.IntentCompat
import com.ilieinc.core.ui.theme.AppTheme
import com.ilieinc.dontsleep.service.tile.MediaTimeoutTileService
import com.ilieinc.dontsleep.service.tile.WakeLockTileService
import com.ilieinc.dontsleep.ui.MediaTimeoutTimerCard
import com.ilieinc.dontsleep.ui.WakeLockTimerCard
import com.ilieinc.dontsleep.viewmodel.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TileQuickSettingsDialogActivity : ComponentActivity() {

    @Inject
    lateinit var mainActivityViewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (mainActivityViewModel.activityRunning) finish()
        val componentName = getComponentNameForLaunchingTile(intent)
        if (componentName.isNullOrEmpty()) {
            finish()
        } else {
            setContent {
                AppTheme {
                    val scrollState = rememberScrollState()
                    Body(
                        componentName = componentName,
                        modifier = Modifier.verticalScroll(scrollState)
                    )
                }
            }
        }
    }

    @Composable
    fun Body(
        componentName: String,
        modifier: Modifier = Modifier
    ) {
        Column(modifier = modifier) {
            when (componentName) {
                WakeLockTileService::class.qualifiedName -> {
                    WakeLockTimerCard(
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                MediaTimeoutTileService::class.qualifiedName -> {
                    MediaTimeoutTimerCard(
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                else -> finish()
            }

            Button(
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth(),
                onClick = ::finish
            ) {
                Text(stringResource(R.string.close))
            }
        }
    }

    private fun getComponentNameForLaunchingTile(intent: Intent) =
        IntentCompat.getParcelableExtra(
            intent, if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Intent.EXTRA_COMPONENT_NAME
            } else {
                "android.intent.extra.COMPONENT_NAME"
            }, ComponentName::class.java
        )?.className
}