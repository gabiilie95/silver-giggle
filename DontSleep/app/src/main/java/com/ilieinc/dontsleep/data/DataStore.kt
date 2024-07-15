package com.ilieinc.dontsleep.data

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey

object DontSleepDataStore {
    val MEDIA_TIMEOUT_PREF_KEY = longPreferencesKey("media_timeout")
    val MEDIA_TIMEOUT_ENABLED_PREF_KEY = booleanPreferencesKey("media_timeout_enabled")
    val WAKE_LOCK_TIMEOUT_PREF_KEY = longPreferencesKey("wake_lock_timeout")
    val WAKE_LOCK_TIMEOUT_ENABLED_PREF_KEY = booleanPreferencesKey("wake_lock_timeout_enabled")
}