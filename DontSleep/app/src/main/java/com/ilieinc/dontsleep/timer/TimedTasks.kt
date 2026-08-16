package com.ilieinc.dontsleep.timer

import android.content.Context
import android.content.Intent
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.ilieinc.core.util.Logger
import com.ilieinc.dontsleep.service.MediaTimeoutService
import com.ilieinc.dontsleep.service.WakeLockService

/**
 * What a scheduled timer actually does when it fires.
 *
 * Kept apart from the workers because [TimerManager] runs these from a plain broadcast
 * receiver on devices where WorkManager cannot be created at all, and both routes have to
 * behave the same.
 */
object TimedTasks {
    /** Stops [serviceName], which is expected to be one of this app's foreground services. */
    fun stopService(context: Context, serviceName: String?) {
        Logger.info("Executing stop service task for $serviceName")
        val serviceClass = when (serviceName) {
            WakeLockService::class.java.name -> WakeLockService::class.java
            MediaTimeoutService::class.java.name -> MediaTimeoutService::class.java
            else -> {
                Logger.info("Ignoring stop service task for unknown service $serviceName")
                return
            }
        }
        context.stopService(Intent(context, serviceClass))
    }

    /**
     * Takes audio focus and immediately hands it back, which is what makes whatever is
     * playing pause.
     */
    fun pauseMediaPlayback(context: Context) {
        Logger.info("Executing media pause task")
        val audioManager = ContextCompat.getSystemService(context, AudioManager::class.java)
        if (audioManager == null) {
            Logger.info("No AudioManager available, skipping media pause task")
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            lateinit var request: AudioFocusRequest
            request = with(AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)) {
                setOnAudioFocusChangeListener { focusChange ->
                    when (focusChange) {
                        AudioManager.AUDIOFOCUS_GAIN -> {
                            audioManager.abandonAudioFocusRequest(request)
                        }

                        else -> {}
                    }
                }
                build()
            }
            audioManager.requestAudioFocus(request)
        } else {
            // AudioFocusRequest is API 26+, and this app still runs on 24.
            lateinit var listener: AudioManager.OnAudioFocusChangeListener
            listener = AudioManager.OnAudioFocusChangeListener { focusChange ->
                when (focusChange) {
                    AudioManager.AUDIOFOCUS_GAIN -> {
                        @Suppress("DEPRECATION")
                        audioManager.abandonAudioFocus(listener)
                    }

                    else -> {}
                }
            }
            @Suppress("DEPRECATION")
            audioManager.requestAudioFocus(
                listener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )
        }
    }
}
