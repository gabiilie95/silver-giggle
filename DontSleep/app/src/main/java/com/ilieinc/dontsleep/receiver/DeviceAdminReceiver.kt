package com.ilieinc.dontsleep.receiver

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import com.ilieinc.dontsleep.model.DeviceAdminChangedEvent
import com.ilieinc.kotlinevents.Event

class DeviceAdminReceiver : DeviceAdminReceiver() {
    companion object {
        val statusChangedEvent = Event(DeviceAdminChangedEvent::class.java)
    }

    override fun onEnabled(context: Context, intent: Intent) {
        statusChangedEvent.invoke(true)
        super.onEnabled(context, intent)
    }

    override fun onDisabled(context: Context, intent: Intent) {
        statusChangedEvent.invoke(false)
        super.onDisabled(context, intent)
    }
}