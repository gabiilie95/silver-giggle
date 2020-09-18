package com.ilieinc.dontsleep.util

import android.util.Log

object Logger {
    private const val TAG = "DontSleep"

    fun error(ex: Throwable) {
        Log.e(TAG, "Error!", ex)
    }

    fun error(message: String? = null, ex: Throwable) {
        Log.e(TAG, message, ex)
    }

    fun debug(message: String) {
        Log.d(TAG, message)
    }

    fun info(message: String) {
        Log.i(TAG, message)
    }
}