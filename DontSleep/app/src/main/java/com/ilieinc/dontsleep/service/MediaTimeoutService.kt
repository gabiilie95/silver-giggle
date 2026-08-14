package com.ilieinc.dontsleep.service

import com.ilieinc.dontsleep.manager.MediaTimeoutServiceManager
import com.ilieinc.dontsleep.timer.MediaTimeoutWorker
import com.ilieinc.dontsleep.timer.TimerManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

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

        private val _serviceRunning = MutableStateFlow(false)
        val serviceRunning = _serviceRunning.asStateFlow()

        fun isRunning() = _serviceRunning.value
    }

    override val binder: ServiceBinder = ServiceBinder(this)
    override val runningState = _serviceRunning

    override fun onServiceStateReady() {
        // Waits for the persisted state: the worker is scheduled off timeoutDateTime.
        TimerManager.setTimedTask<MediaTimeoutWorker>(
            this,
            serviceManager.timeoutDateTime.time,
            MEDIA_TIMEOUT_SERVICE_STOP_TAG
        )
    }

    override fun onServiceStopping() {
        TimerManager.cancelTask(this, MEDIA_TIMEOUT_SERVICE_STOP_TAG)
    }
}
