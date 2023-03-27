package com.ilieinc.dontsleep.timer

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.ilieinc.dontsleep.service.MediaTimeoutService
import com.ilieinc.dontsleep.service.ScreenTimeoutService
import com.ilieinc.dontsleep.service.WakeLockService
import com.ilieinc.dontsleep.util.Logger

class StopServiceWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {
    companion object {
        const val SERVICE_NAME_EXTRA = "ServiceName"
    }

    override fun doWork(): Result {
        var result = Result.success()
        try {
            val service = inputData.getString(SERVICE_NAME_EXTRA)
            Logger.info("Executing StopServiceWorker for $service")
            var serviceIntent: Intent? = null
            when (service) {
                WakeLockService::class.java.name -> {
                    serviceIntent = Intent(applicationContext, WakeLockService::class.java)
                }
                ScreenTimeoutService::class.java.name -> {
                    serviceIntent = Intent(applicationContext, ScreenTimeoutService::class.java)
                }
                MediaTimeoutService::class.java.name -> {
                    serviceIntent = Intent(applicationContext, MediaTimeoutService::class.java)
                }
            }
            serviceIntent?.let {
                applicationContext.stopService(it)
            }
        } catch (ex: Exception) {
            Logger.error(ex)
            result = Result.failure()
        }
        return result
    }
}