package com.ilieinc.dontsleep.timer

import android.content.Context
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ilieinc.dontsleep.util.Logger

@RequiresApi(Build.VERSION_CODES.O)
class MediaTimeoutWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        var result = Result.success()
        try {
            lateinit var request: AudioFocusRequest
            val audioManager =
                ContextCompat.getSystemService(applicationContext, AudioManager::class.java)!!
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
        } catch (ex: Exception) {
            Logger.error(ex)
            result = Result.failure()
        }
        return result
    }
}