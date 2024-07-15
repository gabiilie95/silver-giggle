package com.ilieinc.core.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "DontSleep")

suspend fun <T : Any> DataStore<Preferences>.getValue(key: Preferences.Key<T>, defaultValue: T) =
    data.map { preferences ->
        preferences[key]
    }.firstOrNull() ?: defaultValue

fun <T : Any> DataStore<Preferences>.getValueSynchronous(
    key: Preferences.Key<T>,
    defaultValue: T
) = runBlocking { getValue(key, defaultValue) }

suspend fun <T : Any> DataStore<Preferences>.setValue(key: Preferences.Key<T>, value: T) {
    edit { preferences ->
        preferences[key] = value
    }
}

object CoreDataStore {
    val APP_START_COUNT_PREF_KEY = intPreferencesKey("app_start_count")
    val RATING_SHOWN_PREF_KEY = booleanPreferencesKey("rating_shown")
    val SHOULD_USE_DYNAMIC_COLORS_PREF_KEY = booleanPreferencesKey("should_use_dynamic_colors")
    val PERMISSION_NOTIFICATION_SHOWN_PREF_KEY = booleanPreferencesKey("permission_notification_shown")
}
