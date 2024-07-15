package com.ilieinc.dontsleep.service

import android.content.Context
import com.ilieinc.core.util.StateHelper
import com.ilieinc.dontsleep.manager.MediaTimeoutServiceManager
import com.ilieinc.dontsleep.timer.MediaTimeoutWorker
import com.ilieinc.dontsleep.timer.TimerManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.*

class MediaTimeoutService : BaseService(
    serviceManager = MediaTimeoutServiceManager(
        serviceClass = MediaTimeoutService::class.java,
        serviceTag = MEDIA_TIMEOUT_TAG,
        serviceId = 3
    )
) {
    companion object {
        const val MEDIA_TIMEOUT_TAG = "DontSleep::MediaTimeoutTag"
        const val MEDIA_TIMEOUT_SERVICE_STOP_TAG = "DontSleep::MediaTimeoutServiceStopTag"

        fun isRunning(context: Context) =
            StateHelper.isServiceRunning(context, MediaTimeoutService::class.java)

        private val _serviceRunning = MutableStateFlow(false)
        val serviceRunning = _serviceRunning.asStateFlow()
    }

    override val binder: ServiceBinder = ServiceBinder(this)

    override fun onCreate() {
        super.onCreate()
        _serviceRunning.update { true }
        TimerManager.setTimedTask<MediaTimeoutWorker>(
            this,
            serviceManager.timeoutDateTime.time,
            MEDIA_TIMEOUT_SERVICE_STOP_TAG
        )
    }

    override fun onDestroy() {
        TimerManager.cancelTask(this, MEDIA_TIMEOUT_SERVICE_STOP_TAG)
        _serviceRunning.update { false }
        super.onDestroy()
    }
}