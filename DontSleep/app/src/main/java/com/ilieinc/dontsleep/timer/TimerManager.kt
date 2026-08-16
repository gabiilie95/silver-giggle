package com.ilieinc.dontsleep.timer

import android.app.job.JobScheduler
import android.content.Context
import android.os.Build
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.ilieinc.core.util.Logger
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object TimerManager {

    /**
     * WorkManager builds a SystemJobScheduler the moment it is created, and on API 34+ that
     * constructor calls JobScheduler.forNamespace(). A few ROMs report 34+ without shipping
     * that method, so the call throws NoSuchMethodError and every timer on those devices
     * dies with it. Looking the method up once, before WorkManager is ever touched, keeps
     * those devices on [AlarmTimerScheduler] instead.
     *
     * Set this to false to run the alarm path on a normal device.
     */
    private val workManagerAvailable: Boolean by lazy {
        val available = Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE ||
                runCatching {
                    JobScheduler::class.java.getMethod("forNamespace", String::class.java)
                }.isSuccess
        if (!available) {
            Logger.info("JobScheduler.forNamespace() is missing, using alarms for timed tasks")
        }
        available
    }

    /**
     * Sets an alarm at the specified time.
     */
    inline fun <reified T : ListenableWorker> setTimedTask(
        context: Context,
        requestTime: Date,
        tag: String? = null,
        extras: MutableMap<String, Any>? = null,
        constraints: Constraints? = null
    ) = setTimedTask(T::class.java, context, requestTime, tag, extras, constraints)

    fun <T : ListenableWorker> setTimedTask(
        workerClass: Class<T>,
        context: Context,
        requestTime: Date,
        tag: String? = null,
        extras: MutableMap<String, Any>? = null,
        constraints: Constraints? = null
    ) {
        val taskTag = tag ?: workerClass.simpleName
        val delay = requestTime.time - System.currentTimeMillis()
        Logger.info(
            "Queueing up task: ${workerClass.simpleName} at: ${
                SimpleDateFormat(
                    "MM-dd-yyyy hh:mm:ss aa",
                    Locale.US
                ).format(requestTime.time)
            }."
        )
        val enqueued = workManagerAvailable &&
                enqueueWork(workerClass, context, taskTag, delay, extras, constraints)
        if (!enqueued) {
            AlarmTimerScheduler.schedule(context, workerClass, taskTag, delay, extras)
        }
    }

    fun cancelTask(context: Context, tag: String) {
        Logger.info("Cancelling work by tag $tag")
        if (workManagerAvailable) {
            runCatching { WorkManager.getInstance(context).cancelAllWorkByTag(tag) }
                .onFailure { Logger.error("Could not cancel work for task $tag", it) }
        }
        // Cancelling an alarm that was never set is a no-op, so this covers both the
        // devices that fall back and the ones that failed over mid-session.
        AlarmTimerScheduler.cancel(context, tag)
    }

    /**
     * Returns whether the request made it into WorkManager. A failure here is not fatal:
     * the caller falls back to an alarm, which is also the point of catching [Throwable] —
     * a broken JobScheduler surfaces as an Error rather than an Exception.
     */
    private fun <T : ListenableWorker> enqueueWork(
        workerClass: Class<T>,
        context: Context,
        tag: String,
        delayMillis: Long,
        extras: Map<String, Any>?,
        constraints: Constraints?
    ) = runCatching {
        val request = OneTimeWorkRequest.Builder(workerClass)
        request.addTag(tag)
        if (extras != null) {
            val data = Data.Builder()
            data.putAll(extras)
            request.setInputData(data.build())
        }
        if (constraints != null) {
            request.setConstraints(constraints)
        }
        request.setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
        WorkManager.getInstance(context).enqueue(request.build())
    }.onFailure {
        Logger.error("Could not enqueue task $tag, falling back to an alarm", it)
    }.isSuccess
}
