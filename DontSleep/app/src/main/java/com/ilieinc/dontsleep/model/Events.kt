package com.ilieinc.dontsleep.model

import com.ilieinc.kotlinevents.IEvent

interface ServiceStatusChangedEvent : IEvent {
    fun onServiceStatusChanged(serviceName: String, active: Boolean)
}

interface DeviceAdminChangedEvent : IEvent {
    fun onDeviceAdminStatusChanged(active: Boolean)
}