package com.ilieinc.dontsleep.service.tile

import android.graphics.drawable.Icon
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.widget.Toast
import com.google.gson.Gson
import com.ilieinc.core.data.dataStore
import com.ilieinc.core.data.getValueSynchronous
import com.ilieinc.dontsleep.R
import com.ilieinc.dontsleep.data.DontSleepDataStore
import com.ilieinc.dontsleep.service.WakeLockService
import com.ilieinc.dontsleep.ui.model.CardUiState
import com.ilieinc.core.util.DeviceAdminHelper
import com.ilieinc.core.util.PermissionHelper
import com.ilieinc.core.util.StateHelper.TileStates
import com.ilieinc.core.util.StateHelper.startForegroundService
import com.ilieinc.core.util.StateHelper.stopService

class WakeLockTileService : TileService() {

    private val enabled
        get() = WakeLockService.isRunning(this)

    override fun onClick() {
        if (!isStatusButtonEnabled()) {
            Toast.makeText(applicationContext, "Select a valid time first", Toast.LENGTH_SHORT).show()
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
        super.onStartListening()
    }

    override fun onStopListening() {
        refreshTileState()
        super.onStopListening()
    }

    private fun refreshTileState() {
        val permissionMissing = PermissionHelper.shouldRequestDrawOverPermission(this)
        val statusEnabled = isStatusButtonEnabled()
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

    private fun isStatusButtonEnabled() = runCatching {
        val json = applicationContext.dataStore.getValueSynchronous(
            DontSleepDataStore.WAKE_LOCK_STATE_PREF_KEY,
            ""
        )
        if (json.isEmpty()) {
            CardUiState().statusButtonEnabled
        } else {
            Gson().fromJson(json, CardUiState::class.java).statusButtonEnabled
        }
    }.getOrDefault(true)
}