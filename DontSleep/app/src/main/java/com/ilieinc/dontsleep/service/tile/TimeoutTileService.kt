package com.ilieinc.dontsleep.service.tile

import android.content.Intent
import android.graphics.drawable.Icon
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.core.content.ContextCompat
import com.ilieinc.dontsleep.R
import com.ilieinc.dontsleep.service.TimeoutService
import com.ilieinc.dontsleep.util.DeviceAdminHelper
import com.ilieinc.dontsleep.util.StateHelper.SERVICE_ENABLED_EXTRA

class TimeoutTileService : TileService() {

    private val enabled
        get() = TimeoutService.isRunning(this)

    override fun onClick() {
        val intent = Intent(applicationContext, TimeoutService::class.java)
        intent.putExtra(SERVICE_ENABLED_EXTRA, !enabled)
        ContextCompat.startForegroundService(this, intent)
        changeTileState(enabled)
    }

    override fun onStartListening() {
        DeviceAdminHelper.init(applicationContext)
        changeTileState(enabled)
        super.onStartListening()
    }

    override fun onStopListening() {
        changeTileState(enabled)
        super.onStopListening()
    }


    private fun changeTileState(enabled: Boolean) {
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