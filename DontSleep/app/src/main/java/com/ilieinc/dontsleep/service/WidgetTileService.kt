package com.ilieinc.dontsleep.service

import android.graphics.drawable.Icon
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.ilieinc.dontsleep.R
import com.ilieinc.dontsleep.util.DeviceAdminHelper

class WidgetTileService : TileService() {

    override fun onClick() {
        WakeLockManager.toggleWakeLock(
            applicationContext,
            WakeLockManager.AWAKE_WAKELOCK_TAG,
            false
        )
        changeTileState(WakeLockManager.isWakeLockActive(WakeLockManager.AWAKE_WAKELOCK_TAG))
    }

    override fun onStartListening() {
        DeviceAdminHelper.init(applicationContext)
        changeTileState(WakeLockManager.isWakeLockActive(WakeLockManager.AWAKE_WAKELOCK_TAG))
        super.onStartListening()
    }

    override fun onStopListening() {
        changeTileState(WakeLockManager.isWakeLockActive(WakeLockManager.AWAKE_WAKELOCK_TAG))
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