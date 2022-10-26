package com.ilieinc.donotdisturbsync.receiver

import android.app.NotificationManager.ACTION_INTERRUPTION_FILTER_CHANGED
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.ilieinc.common.util.Logger

class NotificationPolicyChangeReceiver : BroadcastReceiver() {
    companion object{
        fun registerReceiver(context: Context) {
            context.registerReceiver(
                NotificationPolicyChangeReceiver(),
                IntentFilter(ACTION_INTERRUPTION_FILTER_CHANGED)
            )
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.apply {
            if (action == ACTION_INTERRUPTION_FILTER_CHANGED) {
                com.ilieinc.common.util.Logger.info("It Works! $extras")
            }
        }
    }
}