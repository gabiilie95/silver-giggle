package com.ilieinc.dontsleep.service.tile

import android.graphics.drawable.Icon
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.widget.Toast
import com.ilieinc.core.data.dataStore
import com.ilieinc.core.data.getValue
import com.ilieinc.core.util.DeviceAdminHelper
import com.ilieinc.core.util.StateHelper.TileStates
import com.ilieinc.core.util.StateHelper.startForegroundService
import com.ilieinc.core.util.StateHelper.stopService
import com.ilieinc.dontsleep.R
import com.ilieinc.dontsleep.util.JsonUtils.deserializeFromJson
import com.ilieinc.dontsleep.data.DontSleepDataStore
import com.ilieinc.dontsleep.service.MediaTimeoutService
import com.ilieinc.dontsleep.ui.model.CardUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MediaTimeoutTileService : TileService() {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var cachedStatusEnabled = true

    private val enabled
        get() = MediaTimeoutService.isRunning(this)

    override fun onClick() {
        if (!cachedStatusEnabled) {
            Toast.makeText(applicationContext, getString(R.string.invalid_time_selected), Toast.LENGTH_SHORT).show()
            refreshTileState()
            return
        }
        if (!enabled) {
            startForegroundService<MediaTimeoutService>()
        } else {
            stopService<MediaTimeoutService>()
        }
        refreshTileState()
    }

    override fun onStartListening() {
        DeviceAdminHelper.init(applicationContext)
        refreshTileState()
        scope.launch {
            cachedStatusEnabled = loadStatusButtonEnabled()
            launch(Dispatchers.Main) { refreshTileState() }
        }
        super.onStartListening()
    }

    override fun onStopListening() {
        refreshTileState()
        super.onStopListening()
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    private fun refreshTileState() {
        val statusEnabled = cachedStatusEnabled
        val tileState = when {
            !statusEnabled -> TileStates.Disabled
            enabled -> TileStates.On
            else -> TileStates.Off
        }
        qsTile.apply {
            when (tileState) {
                TileStates.On -> {
                    label = "Media Timer Enabled"
                    state = Tile.STATE_ACTIVE
                    icon = Icon.createWithResource(
                        this@MediaTimeoutTileService,
                        R.drawable.baseline_timer_24
                    )
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) subtitle = null
                }

                TileStates.Off -> {
                    label = "Media Timer Disabled"
                    state = Tile.STATE_INACTIVE
                    icon = Icon.createWithResource(
                        this@MediaTimeoutTileService,
                        R.drawable.baseline_timer_off_24
                    )
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) subtitle = null
                }

                TileStates.Disabled -> {
                    label = "Media Timer Disabled"
                    state = Tile.STATE_UNAVAILABLE
                    icon = Icon.createWithResource(
                        this@MediaTimeoutTileService,
                        R.drawable.baseline_timer_off_24
                    )
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) subtitle =
                        "Invalid time selected"
                }
            }
            updateTile()
        }
    }

    private suspend fun loadStatusButtonEnabled(): Boolean = runCatching {
        val json = applicationContext.dataStore.getValue(
            DontSleepDataStore.MEDIA_STATE_PREF_KEY,
            ""
        )
        (json.deserializeFromJson<CardUiState>() ?: CardUiState()).statusButtonEnabled
    }.getOrDefault(true)
}