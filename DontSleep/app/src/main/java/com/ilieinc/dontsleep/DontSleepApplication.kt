package com.ilieinc.dontsleep

import android.app.Application
import androidx.work.Configuration
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DontSleepApplication : Application(), Configuration.Provider {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }

    /**
     * WorkManager is deliberately not started by androidx.startup (see AndroidManifest.xml),
     * so this is what it initializes itself from, the first time a timer actually needs it.
     */
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().build()
}
