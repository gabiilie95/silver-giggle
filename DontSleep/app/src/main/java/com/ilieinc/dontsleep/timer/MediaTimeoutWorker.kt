package com.ilieinc.dontsleep.timer

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ilieinc.core.util.Logger

class MediaTimeoutWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result = runCatching {
        TimedTasks.pauseMediaPlayback(applicationContext)
        Result.success()
    }.getOrElse { ex ->
        Logger.error("Error running MediaTimeoutWorker", ex)
        Result.failure()
    }
}
