package com.ilieinc.dontsleep.timer

import android.content.Context
import android.content.Intent
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.ilieinc.dontsleep.service.TimeoutService
import com.ilieinc.dontsleep.util.Logger

class StopServiceWorker(
    private val context: Context,
    params: WorkerParameters
) : Worker(context, params) {
    override fun doWork(): Result {
        var result = Result.success()
        try {
            context.stopService(Intent(context, TimeoutService::class.java))
        } catch (ex: Exception) {
            Logger.error(ex)
            result = Result.failure()
        }
        return result
    }
}