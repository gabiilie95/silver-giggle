package com.ilieinc.dontsleep.util

import android.content.Context
import com.ilieinc.core.data.dataStore
import com.ilieinc.core.data.getValue
import com.ilieinc.core.data.setValue
import com.ilieinc.core.util.Logger
import com.ilieinc.dontsleep.data.DontSleepDataStore.UPDATE_NOTICE_SHOWN_PREF_KEY

/**
 * Decides whether to show the one-off note about the recent crashes. It is only meant for
 * people who already had the app installed, and only the first time they open it after an
 * update.
 */
object UpdateNoticeHelper {

    suspend fun needToShowUpdateNotice(context: Context): Boolean {
        if (context.dataStore.getValue(UPDATE_NOTICE_SHOWN_PREF_KEY, false)) {
            return false
        }
        if (isUpgrade(context)) {
            return true
        }
        // A fresh install has nothing to apologise for. Mark it as seen now, otherwise the
        // note would appear the first time this user updates to some later version.
        markUpdateNoticeShown(context)
        return false
    }

    suspend fun markUpdateNoticeShown(context: Context) {
        context.dataStore.setValue(UPDATE_NOTICE_SHOWN_PREF_KEY, true)
    }

    /** True once the app has been updated at least once since it was installed. */
    private fun isUpgrade(context: Context) = runCatching {
        @Suppress("DEPRECATION")
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        packageInfo.lastUpdateTime > packageInfo.firstInstallTime
    }.getOrElse {
        Logger.error("Could not read the install times, skipping the update notice", it)
        false
    }
}
