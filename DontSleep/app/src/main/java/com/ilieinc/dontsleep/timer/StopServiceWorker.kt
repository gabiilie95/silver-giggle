package com.ilieinc.dontsleep.timer

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.ilieinc.core.util.Logger

class StopServiceWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {
    companion object {
        const val SERVICE_NAME_EXTRA = "ServiceName"
    }

    override fun doWork(): Result = runCatching {
        TimedTasks.stopService(applicationContext, inputData.getString(SERVICE_NAME_EXTRA))
        Result.success()
    }.getOrElse { ex ->
        Logger.error("Error running StopServiceWorker", ex)
        Result.failure()
    }
}
