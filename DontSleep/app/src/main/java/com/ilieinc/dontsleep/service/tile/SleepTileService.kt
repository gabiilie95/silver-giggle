package com.ilieinc.dontsleep.service.tile

import android.app.admin.DevicePolicyManager
import android.graphics.drawable.Icon
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.core.content.ContextCompat
import com.ilieinc.dontsleep.R
import com.ilieinc.dontsleep.service.SleepService
import com.ilieinc.dontsleep.util.DeviceAdminHelper
import com.ilieinc.dontsleep.util.StateHelper
import com.ilieinc.dontsleep.util.StateHelper.startForegroundService
import com.ilieinc.dontsleep.util.StateHelper.stopService

class SleepTileService : TileService() {

    enum class TileStates {
        On,
        Off,
        Disabled,
    }

    private val enabled
        get() = StateHelper.isServiceRunning(this, SleepService::class.java)

    override fun onClick() {
        if (!enabled) {
            startForegroundService<SleepService>()
        } else {
            stopService<SleepService>()
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
        val deviceManager =
            ContextCompat.getSystemService(applicationContext, DevicePolicyManager::class.java)!!
        val adminActive = deviceManager.isAdminActive(DeviceAdminHelper.componentName)
        val state = if (adminActive) {
            if (enabled) {
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