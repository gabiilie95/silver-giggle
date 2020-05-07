package com.ilieinc.dontsleep.model

import com.ilieinc.kotlinevents.IEvent

interface WakeLockChangedEvent : IEvent {
    fun onWakeLockChanged(wakeLock: String, active: Boolean)
}

interface DeviceAdminChangedEvent : IEvent {
    fun onDeviceAdminStatusChanged(active: Boolean)
}