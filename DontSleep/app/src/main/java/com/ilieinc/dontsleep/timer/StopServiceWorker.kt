package com.ilieinc.dontsleep.timer

import android.content.Context
import android.content.Intent
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.ilieinc.dontsleep.service.SleepService
import com.ilieinc.dontsleep.service.TimeoutService
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
            var serviceIntent: Intent? = null
            when (service) {
                TimeoutService::class.java.name -> {
                    serviceIntent = Intent(applicationContext, TimeoutService::class.java)
                }
                SleepService::class.java.name -> {
                    serviceIntent = Intent(applicationContext, SleepService::class.java)
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