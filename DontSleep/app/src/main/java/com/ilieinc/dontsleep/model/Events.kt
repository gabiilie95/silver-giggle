package com.ilieinc.dontsleep.model

import com.ilieinc.kotlinevents.IEvent

interface DeviceAdminChangedEvent : IEvent {
    fun onDeviceAdminStatusChanged(active: Boolean)
}