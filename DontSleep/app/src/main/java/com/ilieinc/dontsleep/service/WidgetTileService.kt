package com.ilieinc.dontsleep.service

import android.graphics.drawable.Icon
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.ilieinc.dontsleep.R

class WidgetTileService : TileService() {

    override fun onClick() {
        WakeLockManager.toggleAwakeWakeLock(applicationContext)
        changeTileState(WakeLockManager.awakeTimerActive)
    }

    override fun onStartListening() {
        changeTileState(WakeLockManager.awakeTimerActive)
        super.onStartListening()
    }

    override fun onStopListening() {
        changeTileState(WakeLockManager.awakeTimerActive)
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