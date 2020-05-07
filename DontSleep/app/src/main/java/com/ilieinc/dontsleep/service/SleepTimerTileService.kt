package com.ilieinc.dontsleep.service

import android.app.admin.DevicePolicyManager
import android.graphics.drawable.Icon
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.core.content.ContextCompat
import com.ilieinc.dontsleep.R
import com.ilieinc.dontsleep.util.DeviceAdminHelper

class SleepTimerTileService : TileService() {

    enum class TileStates {
        On,
        Off,
        Disabled,
    }

    override fun onClick() {
        WakeLockManager.toggleSleepTimerWakeLock(applicationContext)
        refreshTileState()
    }

    override fun onStartListening() {
        refreshTileState()
        super.onStartListening()
    }

    override fun onStopListening() {
        refreshTileState()
        super.onStopListening()
    }

    private fun refreshTileState() {
        val deviceManager =
            ContextCompat.getSystemService(applicationContext, DevicePolicyManager::class.java)!!
        val adminActive = deviceManager.isAdminActive(DeviceAdminHelper.componentName)
        val state = if (adminActive) {
            if (WakeLockManager.sleepTimerActive) {
                TileStates.On
            } else {
                TileStates.Off
            }
        } else {
            TileStates.Disabled
        }

        val tile = qsTile
        when (state) {
            TileStates.On -> {
                tile.label = "Staying Awake!"
                tile.state = Tile.STATE_ACTIVE
                tile.icon = Icon.createWithResource(this, R.drawable.baseline_timer_24)
            }
            TileStates.Off -> {
                tile.label = "Timer Disabled"
                tile.state = Tile.STATE_INACTIVE
                tile.icon = Icon.createWithResource(this, R.drawable.baseline_timer_off_24)
            }
            TileStates.Disabled -> {
                tile.label = "Timer Disabled"
                tile.state = Tile.STATE_UNAVAILABLE
                tile.icon = Icon.createWithResource(this, R.drawable.baseline_timer_off_24)
            }
        }
        tile.updateTile()
    }
}