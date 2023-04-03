package com.ilieinc.dontsleep.service.tile

import android.graphics.drawable.Icon
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.ilieinc.dontsleep.R
import com.ilieinc.dontsleep.service.WakeLockService
import com.ilieinc.core.util.DeviceAdminHelper
import com.ilieinc.core.util.PermissionHelper
import com.ilieinc.core.util.StateHelper.TileStates
import com.ilieinc.core.util.StateHelper.startForegroundService
import com.ilieinc.core.util.StateHelper.stopService

class WakeLockTileService : TileService() {

    private val enabled
        get() = WakeLockService.isRunning(this)

    override fun onClick() {
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
        val tileState = if (PermissionHelper.shouldRequestDrawOverPermission(this)) {
            TileStates.Disabled
        } else {
            if (enabled) {
                TileStates.On
            } else {
                TileStates.Off
            }
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
                }
                TileStates.Off -> {
                    label = "Sleep..."
                    state = Tile.STATE_INACTIVE
                    icon = Icon.createWithResource(
                        this@WakeLockTileService,
                        R.drawable.baseline_mobile_off_24
                    )
                }
                TileStates.Disabled -> {
                    label = "Sleep..."
                    state = Tile.STATE_UNAVAILABLE
                    icon = Icon.createWithResource(
                        this@WakeLockTileService,
                        R.drawable.baseline_mobile_off_24
                    )
                }
            }
            updateTile()
        }
    }
}