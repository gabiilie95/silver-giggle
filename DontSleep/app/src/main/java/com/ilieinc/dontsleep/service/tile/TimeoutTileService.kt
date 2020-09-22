package com.ilieinc.dontsleep.service.tile

import android.graphics.drawable.Icon
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.ilieinc.dontsleep.R
import com.ilieinc.dontsleep.service.TimeoutService
import com.ilieinc.dontsleep.util.DeviceAdminHelper
import com.ilieinc.dontsleep.util.StateHelper.startForegroundService
import com.ilieinc.dontsleep.util.StateHelper.stopService

class TimeoutTileService : TileService() {

    private val enabled
        get() = TimeoutService.isRunning(this)

    override fun onClick() {
        if (!enabled) {
            startForegroundService<TimeoutService>()
        } else {
            stopService<TimeoutService>()
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
        val tile = qsTile
        when (enabled) {
            true -> {
                tile.label = "Don't Sleep!"
                tile.state = Tile.STATE_ACTIVE
                tile.icon = Icon.createWithResource(this, R.drawable.baseline_mobile_friendly_24)
            }
            false -> {
                tile.label = "Sleep..."
                tile.state = Tile.STATE_INACTIVE
                tile.icon = Icon.createWithResource(this, R.drawable.baseline_mobile_off_24)
            }
        }
        tile.updateTile()
    }
}