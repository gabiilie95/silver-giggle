package com.ilieinc.dontsleep.service.tile

import android.graphics.drawable.Icon
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.widget.Toast
import com.ilieinc.core.data.dataStore
import com.ilieinc.core.data.getValue
import com.ilieinc.dontsleep.R
import com.ilieinc.dontsleep.util.JsonUtils.deserializeFromJson
import com.ilieinc.dontsleep.data.DontSleepDataStore
import com.ilieinc.dontsleep.service.WakeLockService
import com.ilieinc.dontsleep.ui.model.CardUiState
import com.ilieinc.core.util.DeviceAdminHelper
import com.ilieinc.core.util.PermissionHelper
import com.ilieinc.core.util.StateHelper.TileStates
import com.ilieinc.core.util.StateHelper.startForegroundService
import com.ilieinc.core.util.StateHelper.stopService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class WakeLockTileService : TileService() {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var cachedStatusEnabled = true

    private val enabled
        get() = WakeLockService.isRunning(this)

    override fun onClick() {
        if (!cachedStatusEnabled) {
            Toast.makeText(applicationContext, getString(R.string.invalid_time_selected), Toast.LENGTH_SHORT).show()
            refreshTileState()
            return
        }
        if (!enabled) {
            startForegroundService<WakeLockService>()
        } else {
            stopService<WakeLockService>()
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
        val permissionMissing = PermissionHelper.shouldRequestDrawOverPermission(this)
        val statusEnabled = cachedStatusEnabled
        val tileState = when {
            permissionMissing || !statusEnabled -> TileStates.Disabled
            enabled -> TileStates.On
            else -> TileStates.Off
        }
        qsTile.apply {
            when (tileState) {
                TileStates.On -> {
                    label = "Don't Sleep!"
                    state = Tile.STATE_ACTIVE
                    icon = Icon.createWithResource(
                        this@WakeLockTileService,
                        R.drawable.baseline_mobile_friendly_24
                    )
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) subtitle = null
                }
                TileStates.Off -> {
                    label = "Sleep..."
                    state = Tile.STATE_INACTIVE
                    icon = Icon.createWithResource(
                        this@WakeLockTileService,
                        R.drawable.baseline_mobile_off_24
                    )
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) subtitle = null
                }
                TileStates.Disabled -> {
                    label = "Sleep..."
                    state = Tile.STATE_UNAVAILABLE
                    icon = Icon.createWithResource(
                        this@WakeLockTileService,
                        R.drawable.baseline_mobile_off_24
                    )
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        subtitle = if (permissionMissing) {
                            "Permission required"
                        } else {
                            "Invalid time selected"
                        }
                    }
                }
            }
            updateTile()
        }
    }

    private suspend fun loadStatusButtonEnabled(): Boolean = runCatching {
        val json = applicationContext.dataStore.getValue(
            DontSleepDataStore.WAKE_LOCK_STATE_PREF_KEY,
            ""
        )
        (json.deserializeFromJson<CardUiState>() ?: CardUiState()).statusButtonEnabled
    }.getOrDefault(true)
}