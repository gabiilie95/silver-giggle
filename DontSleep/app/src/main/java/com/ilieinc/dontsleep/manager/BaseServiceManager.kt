package com.ilieinc.dontsleep.manager

import android.app.Notification
import android.content.Context
import androidx.datastore.preferences.core.Preferences
import com.google.gson.Gson
import com.ilieinc.core.data.dataStore
import com.ilieinc.core.data.getValueSynchronous
import com.ilieinc.core.util.Logger
import com.ilieinc.dontsleep.timer.StopServiceWorker
import com.ilieinc.dontsleep.timer.TimerManager
import com.ilieinc.dontsleep.ui.model.CardUiState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

abstract class BaseServiceManager(
    private val serviceClass: Class<*>,
    private val serviceStatePreferenceKey: Preferences.Key<String>,
    private val serviceTaskTag: String,
    val serviceId: Int,
) {
    protected lateinit var context: Context

    abstract val foregroundServiceTypeFlag: Int?
    abstract val notification: Notification
    var timeoutDateTime: Calendar = Calendar.getInstance()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        Logger.error("Coroutine exception in $serviceClass: ${exception.message}", exception)
    }
    private val ioScope = Dispatchers.IO + coroutineExceptionHandler

    private var job: Job? = null
    protected var state: CardUiState = CardUiState()
    var timeout: Long = 500000

    fun initContext(context: Context) {
        this.context = context
    }

    open fun onCreateService() {
        Logger.info("Starting service $serviceClass")
        initFields()
        job = initObservers()
        initTimeout(state)
    }

    private fun initFields() {
        state = with(context.dataStore.getValueSynchronous(serviceStatePreferenceKey, "")) {
            Gson().fromJson(this, CardUiState::class.java)
        }
    }

    private fun initObservers() = CoroutineScope(ioScope).launch {
        context.dataStore.data.collectLatest { prefs ->
            prefs[serviceStatePreferenceKey]?.let {
                state = Gson().fromJson(it, CardUiState::class.java)
            }
        }
    }

    private fun initTimeout(state: CardUiState) {
        timeout = if (state.timeoutEnabled) {
            getTimeout(state)
        } else {
            Int.MAX_VALUE.toLong()
        }
        timeoutDateTime = Calendar.getInstance()
        timeoutDateTime.add(Calendar.MILLISECOND, timeout.toInt())
        if (state.timeoutEnabled) {
            TimerManager.setTimedTask<StopServiceWorker>(
                context,
                timeoutDateTime.time,
                serviceTaskTag,
                mutableMapOf(StopServiceWorker.SERVICE_NAME_EXTRA to serviceClass.name)
            )
        }
    }

    private fun getTimeout(state: CardUiState): Long {
        return runCatching {
            val selectedTime = state.selectedTime
            requireNotNull(selectedTime)
            when (state.timeoutMode) {
                CardUiState.TimeoutMode.TIMEOUT -> {
                    (selectedTime.hour * 60 * 60 * 1000 + selectedTime.minute * 60 * 1000).toLong()
                }

                CardUiState.TimeoutMode.CLOCK -> {
                    val currentTime = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, selectedTime.hour)
                        set(Calendar.MINUTE, selectedTime.minute)
                        if (before(Calendar.getInstance()))
                            add(Calendar.DAY_OF_YEAR, 1)
                    }
                    return currentTime.timeInMillis - System.currentTimeMillis()
                }
            }
        }.fold(
            onSuccess = { it },
            onFailure = {
                Logger.error("Error getting timeout", it)
                System.currentTimeMillis()
            }
        )
    }

    open fun onDestroyService() = runCatching {
        TimerManager.cancelTask(context, serviceTaskTag)
        job?.cancel()
    }.onFailure {
        Logger.error("Error stopping service $serviceClass", it)
    }
}