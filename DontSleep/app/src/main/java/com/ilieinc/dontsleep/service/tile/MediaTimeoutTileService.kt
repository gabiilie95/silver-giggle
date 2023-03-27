package com.ilieinc.dontsleep.service.tile

import android.graphics.drawable.Icon
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.ilieinc.dontsleep.R
import com.ilieinc.dontsleep.service.MediaTimeoutService
import com.ilieinc.dontsleep.util.DeviceAdminHelper
import com.ilieinc.dontsleep.util.StateHelper.TileStates
import com.ilieinc.dontsleep.util.StateHelper.startForegroundService
import com.ilieinc.dontsleep.util.StateHelper.stopService

class MediaTimeoutTileService : TileService() {

    private val enabled
        get() = MediaTimeoutService.isRunning(this)

    override fun onClick() {
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
        super.onStartListening()
    }

    override fun onStopListening() {
        refreshTileState()
        super.onStopListening()
    }

    private fun refreshTileState() {
        val tileState = if (enabled) {
            TileStates.On
        } else {
            TileStates.Off
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
                }
                TileStates.Off -> {
                    label = "Media Timer Disabled"
                    state = Tile.STATE_INACTIVE
                    icon = Icon.createWithResource(
                        this@MediaTimeoutTileService,
                        R.drawable.baseline_timer_off_24
                    )
                }
                TileStates.Disabled -> {
                    label = "Media Timer Disabled"
                    state = Tile.STATE_UNAVAILABLE
                    icon = Icon.createWithResource(
                        this@MediaTimeoutTileService,
                        R.drawable.baseline_timer_off_24
                    )
                }
            }
            updateTile()
        }
    }
}