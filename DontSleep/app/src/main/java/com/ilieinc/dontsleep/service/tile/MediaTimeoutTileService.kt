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
import kotlinx.coroutines.withContext

class MediaTimeoutTileService : TileService() {

    private var listeningScope: CoroutineScope? = null
    private var cachedStatusEnabled = true

    override fun onClick() {
        val running = MediaTimeoutService.isRunning()
        if (!running && !cachedStatusEnabled) {
            showToast(R.string.invalid_time_selected)
            refreshTileState()
            return
        }
        if (running) {
            stopService<MediaTimeoutService>()
        } else if (!startForegroundService<MediaTimeoutService>()) {
            // Android can refuse the start outright; say so rather than leaving the tile
            // looking like it worked.
            showToast(R.string.service_start_failed)
        }
        refreshTileState()
    }

    override fun onStartListening() {
        super.onStartListening()
        DeviceAdminHelper.init(applicationContext)
        val scope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())
        listeningScope = scope
        // Follows the service for as long as the tile is visible, so a timeout that stops
        // it in the background shows up without waiting for the next tap. The flow replays
        // its current value, which doubles as the initial render.
        scope.launch {
            MediaTimeoutService.serviceRunning.collect { refreshTileState() }
        }
        scope.launch {
            cachedStatusEnabled = withContext(Dispatchers.IO) { loadStatusButtonEnabled() }
            refreshTileState()
        }
    }

    override fun onStopListening() {
        listeningScope?.cancel()
        listeningScope = null
        super.onStopListening()
    }

    override fun onDestroy() {
        listeningScope?.cancel()
        listeningScope = null
        super.onDestroy()
    }

    private fun showToast(messageId: Int) {
        Toast.makeText(applicationContext, getString(messageId), Toast.LENGTH_SHORT).show()
    }

    private fun refreshTileState() {
        // Null until the tile is bound, and again once it is unbound.
        val tile = qsTile ?: return
        val enabled = MediaTimeoutService.isRunning()
        val tileState = when {
            // A running service always stays stoppable, whatever the saved time says.
            !enabled && !cachedStatusEnabled -> TileStates.Disabled
            enabled -> TileStates.On
            else -> TileStates.Off
        }
        tile.apply {
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
