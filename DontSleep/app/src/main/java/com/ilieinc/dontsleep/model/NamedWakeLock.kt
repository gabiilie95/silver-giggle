package com.ilieinc.dontsleep.model

import android.os.PowerManager
import com.ilieinc.dontsleep.util.Logger

data class NamedWakeLock(
    var name: String = "",
    var lock: PowerManager.WakeLock? = null
) {
    fun release() {
        name = ""
        runCatching {
            lock?.release()
        }.onFailure { ex -> Logger.error(ex) }
        lock = null
    }
}