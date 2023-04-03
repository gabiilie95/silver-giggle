package com.ilieinc.dontsleep.service.tile

import android.app.admin.DevicePolicyManager
import android.graphics.drawable.Icon
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.core.content.ContextCompat
import com.ilieinc.dontsleep.R
import com.ilieinc.dontsleep.service.ScreenTimeoutService
import com.ilieinc.core.util.DeviceAdminHelper
import com.ilieinc.core.util.StateHelper.TileStates
import com.ilieinc.core.util.StateHelper.startForegroundService
import com.ilieinc.core.util.StateHelper.stopService

class ScreenTimeoutTileService : TileService() {

    private val enabled
        get() = ScreenTimeoutService.isRunning(this)

    override fun onClick() {
        if (!enabled) {
            startForegroundService<ScreenTimeoutService>()
        } else {
            stopService<ScreenTimeoutService>()
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
        val tileState = if (adminActive) {
            if (enabled) {
                TileStates.On
            } else {
                TileStates.Off
            }
        } else {
            TileStates.Disabled
        }
        qsTile.apply {
            when (tileState) {
                TileStates.On -> {
                    label = "Staying Awake!"
                    state = Tile.STATE_ACTIVE
                    icon = Icon.createWithResource(
                        this@ScreenTimeoutTileService,
                        R.drawable.baseline_timer_24
                    )
                }
                TileStates.Off -> {
                    label = "Timer Disabled"
                    state = Tile.STATE_INACTIVE
                    icon = Icon.createWithResource(
                        this@ScreenTimeoutTileService,
                        R.drawable.baseline_timer_off_24
                    )
                }
                TileStates.Disabled -> {
                    label = "Timer Disabled"
                    state = Tile.STATE_UNAVAILABLE
                    icon = Icon.createWithResource(
                        this@ScreenTimeoutTileService,
                        R.drawable.baseline_timer_off_24
                    )
                }
            }
            updateTile()
        }
    }
}