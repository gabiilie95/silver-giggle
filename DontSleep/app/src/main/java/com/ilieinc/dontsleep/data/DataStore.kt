package com.ilieinc.dontsleep.data

import androidx.datastore.preferences.core.stringPreferencesKey

object DontSleepDataStore {
    val MEDIA_STATE_PREF_KEY = stringPreferencesKey("media_timeout_state")
    val WAKE_LOCK_STATE_PREF_KEY = stringPreferencesKey("wake_lock_timeout_state")
}