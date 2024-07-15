package com.ilieinc.dontsleep.manager

import android.app.Notification
import android.content.Context
import androidx.datastore.preferences.core.Preferences
import com.ilieinc.core.data.dataStore
import com.ilieinc.core.data.getValueSynchronous
import com.ilieinc.core.util.Logger
import com.ilieinc.dontsleep.timer.StopServiceWorker
import com.ilieinc.dontsleep.timer.TimerManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

abstract class BaseServiceManager(
    private val serviceClass: Class<*>,
    private val serviceTimeoutPreferenceKey: Preferences.Key<Long>,
    private val serviceEnabledPreferenceKey: Preferences.Key<Boolean>,
    private val serviceTaskTag: String,
    val serviceId: Int,
) {
    protected lateinit var context: Context

    abstract val foregroundServiceTypeFlag: Int?
    abstract val notification: Notification
    var timeoutDateTime: Calendar = Calendar.getInstance()

    private var job: Job? = null
    protected var timeoutEnabled: Boolean = true
    var timeout: Long = 500000

    fun initContext(context: Context) {
        this.context = context
    }

    open fun onCreateService() {
        Logger.info("Starting service $serviceClass")
        initFields()
        job = initObservers()
        initTimeout()
    }

    private fun initFields() {
        timeoutEnabled = context.dataStore.getValueSynchronous(serviceEnabledPreferenceKey, true)
    }

    private fun initObservers() = CoroutineScope(Dispatchers.IO).launch {
        context.dataStore.data.collectLatest { prefs ->
            prefs[serviceTimeoutPreferenceKey]?.let{
                timeout = it
            }
            prefs[serviceEnabledPreferenceKey]?.let {
                timeoutEnabled = it
            }
        }
    }

    private fun initTimeout() {
        timeout = if (timeoutEnabled) {
            context.dataStore.getValueSynchronous(serviceTimeoutPreferenceKey, 500000L)
        } else {
            Int.MAX_VALUE.toLong()
        }
        timeoutDateTime = Calendar.getInstance()
        timeoutDateTime.add(Calendar.MILLISECOND, timeout.toInt())
        if (timeoutEnabled) {
            TimerManager.setTimedTask<StopServiceWorker>(
                context,
                timeoutDateTime.time,
                serviceTaskTag,
                mutableMapOf(StopServiceWorker.SERVICE_NAME_EXTRA to serviceClass.name)
            )
        }
    }

    open fun onDestroyService() = runCatching {
        TimerManager.cancelTask(context, serviceTaskTag)
        job?.cancel()
    }.onFailure {
        Logger.error("Error stopping service $serviceClass", it)
    }
}