package com.ilieinc.dontsleep.viewmodel

import androidx.lifecycle.ViewModel

class MainActivityViewModel : ViewModel() {
    var activityRunning: Boolean = false
        private set

    fun setActivityRunning(running: Boolean) {
        activityRunning = running
    }
}