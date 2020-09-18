package com.ilieinc.dontsleep.model

import android.os.PowerManager

data class NamedWakeLock(
    var name: String = "",
    var lock: PowerManager.WakeLock? = null
) {
    fun release() {
        name = ""
        lock?.release()
        lock = null
    }
}