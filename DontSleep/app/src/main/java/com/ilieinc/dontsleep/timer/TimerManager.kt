package com.ilieinc.dontsleep.timer

import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.*
import com.ilieinc.dontsleep.util.Logger
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import java.util.concurrent.TimeUnit

class TimerManager {
    companion object {
        /**
         * Sets an alarm at the specified time.
         */
        inline fun <reified T : Worker> setTimedTask(
            context: Context,
            requestTime: Date,
            tag: String? = null,
            extras: MutableMap<String, Any>? = null,
            constraints: Constraints? = null
        ) {
            Logger.info(
                "Queueing up task: ${T::class.java.simpleName} at: ${
                    SimpleDateFormat(
                        "MM-dd-yyyy hh:mm:ss aa",
                        Locale.US
                    ).format(requestTime.time)
                }."
            )
            val request = OneTimeWorkRequest.Builder(T::class.java)
            request.addTag(tag ?: T::class.java.simpleName)
            if (extras != null) {
                val data = Data.Builder()
                data.putAll(extras)
                request.setInputData(data.build())
            }
            if (constraints != null) {
                request.setConstraints(constraints)
            }
            val requestCalendar = Calendar.getInstance()
            requestCalendar.time = requestTime
            val timeDiff = requestCalendar.timeInMillis - Calendar.getInstance().timeInMillis
            request.setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
            val workManager = WorkManager.getInstance(context)
            workManager.enqueue(request.build())
        }

        inline fun <reified T> cancelTask(context: Context) {
            val wm = WorkManager.getInstance(context)
            wm.cancelAllWorkByTag(T::class.java.simpleName)
        }

        fun cancelTask(context: Context, tag: String) {
            Logger.info("Cancelling work by tag $tag")
            val wm = WorkManager.getInstance(context)
            wm.cancelAllWorkByTag(tag)
        }

        fun setTimer(context: Context, action: Runnable, timerTime: LocalDate) {
            TODO("Make me work")
//            val timeDiff = (Duration.between(timerTime,LocalDate.now())).milliseconds;
//            val uptimeTimeDiff = SystemClock.uptimeMillis() + timeDiff;
//
//            val mainLooperHandler = Handler(context.mainLooper);
//            mainLooperHandler.postAtTime(action, uptimeTimeDiff);
        }

        inline fun <reified T> getPendingIntent(
            context: Context,
            timerIntent: Intent?,
            requestCode: Int,
            flags: Int
        ): PendingIntent? {
            var pi: PendingIntent? = null
            val processedTimerIntent = timerIntent ?: Intent(context, T::class.java)
            if (Service::class.java.isAssignableFrom(T::class.java)) {
                pi = PendingIntent.getService(context, requestCode, processedTimerIntent, flags)
            } else if (BroadcastReceiver::class.java.isAssignableFrom(T::class.java)) {
                pi = PendingIntent.getBroadcast(context, requestCode, processedTimerIntent, flags)
            }
            return pi
        }
    }
}