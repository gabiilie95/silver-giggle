package com.ilieinc.dontsleep.ui.main

import android.content.Context
import androidx.lifecycle.ViewModel
import com.ilieinc.dontsleep.util.SharedPreferenceManager

class MainViewModel : ViewModel() {
    fun saveTimePickerSettings(context: Context, value: String, timeout: Pair<Int, Int>) {
        val editor = SharedPreferenceManager.getInstance(context).edit()
        editor.putLong(value, ((timeout.first * 60 + timeout.second) * 60 * 1000).toLong())
        editor.apply()
    }
}
