package com.ilieinc.donotdisturbsync.util

import android.content.Context
import android.content.SharedPreferences

object SharedPreferenceManager {
    private const val SHARED_PREFERENCES = "DontSleep"

    fun getInstance(context: Context): SharedPreferences {
        return context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
    }
}