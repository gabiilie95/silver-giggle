package com.ilieinc.dontsleep.manager

import android.app.Notification
import android.content.Context
import androidx.datastore.preferences.core.Preferences
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.ilieinc.core.data.dataStore
import com.ilieinc.core.data.getValue
import com.ilieinc.core.util.Logger
import com.ilieinc.dontsleep.util.JsonUtils.deserializeFromJson
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
    companion object {
        /** Used until the persisted state has loaded, and whenever it cannot be read. */
        const val DEFAULT_TIMEOUT = 500_000L

        /** ~24 days, the longest span that still fits [Calendar.add]'s millisecond field. */
        const val INDEFINITE_TIMEOUT = Int.MAX_VALUE.toLong()

        /**
         * Same value as ServiceInfo.FOREGROUND_SERVICE_TYPE_NONE, spelled out because that
         * constant is API 29+ and now deprecated, while the value has always been 0.
         * Only API 34+ requires a real type, so this covers everything below it.
         */
        const val FOREGROUND_SERVICE_TYPE_UNSPECIFIED = 0
    }

    protected lateinit var context: Context

    abstract val foregroundServiceType: Int

    /**
     * Built on demand rather than cached: the service posts one notification before the
     * persisted state is available and refreshes it once the real timeout is known.
     */
    abstract fun buildNotification(): Notification

    var timeoutDateTime: Calendar = Calendar.getInstance()
        private set

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        exception.message?.let { FirebaseCrashlytics.getInstance().log(it) }
        Logger.error("Coroutine exception in $serviceClass: ${exception.message}", exception)
    }
    private val ioScope = Dispatchers.IO + coroutineExceptionHandler

    private var job: Job? = null

    @Volatile
    protected var state: CardUiState = CardUiState()

    @Volatile
    var timeout: Long = DEFAULT_TIMEOUT
        private set

    fun initContext(context: Context) {
        this.context = context
    }

    open suspend fun onCreateService() {
        Logger.info("Starting service $serviceClass")
        initFields()
        job = initObservers()
        initTimeout(state)
    }

    private suspend fun initFields() {
        state = runCatching {
            val json = context.dataStore.getValue(serviceStatePreferenceKey, "")
            json.deserializeFromJson<CardUiState>() ?: CardUiState()
        }.fold(
            onSuccess = { it },
            onFailure = { ex ->
                ex.message?.let {
                    FirebaseCrashlytics.getInstance()
                        .log("Error initializing state for $serviceClass $it")
                }
                Logger.error(ex)
                CardUiState() // Return default state if error occurs
            }
        )
    }

    private fun initObservers() = CoroutineScope(ioScope).launch {
        context.dataStore.data.collectLatest { prefs ->
            prefs[serviceStatePreferenceKey]?.let { json ->
                json.deserializeFromJson<CardUiState>()?.let { state = it }
            }
        }
    }

    private fun initTimeout(state: CardUiState) {
        timeout = if (state.timeoutEnabled) {
            getTimeout(state)
        } else {
            INDEFINITE_TIMEOUT
        }
        timeoutDateTime = Calendar.getInstance().apply {
            add(Calendar.MILLISECOND, timeout.toInt())
        }
        if (state.timeoutEnabled) {
            TimerManager.setTimedTask<StopServiceWorker>(
                context,
                timeoutDateTime.time,
                serviceTaskTag,
                mutableMapOf(StopServiceWorker.SERVICE_NAME_EXTRA to serviceClass.name)
            )
        }
    }

    private fun getTimeout(state: CardUiState): Long = runCatching {
        val selectedTime = requireNotNull(state.selectedTime)
        when (state.timeoutMode) {
            CardUiState.TimeoutMode.TIMEOUT -> {
                (selectedTime.hour * 60 * 60 * 1000 + selectedTime.minute * 60 * 1000).toLong()
            }

            CardUiState.TimeoutMode.CLOCK -> {
                val targetTime = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, selectedTime.hour)
                    set(Calendar.MINUTE, selectedTime.minute)
                    if (before(Calendar.getInstance())) {
                        add(Calendar.DAY_OF_YEAR, 1)
                    }
                }
                targetTime.timeInMillis - System.currentTimeMillis()
            }
        }
    }.fold(
        // A timeout outside this range would overflow the Int that Calendar.add() takes
        // and push the stop time into the past, leaving the service running forever.
        onSuccess = { it.coerceIn(0, INDEFINITE_TIMEOUT) },
        onFailure = {
            Logger.error("Error getting timeout", it)
            DEFAULT_TIMEOUT
        }
    )

    open fun onDestroyService() = runCatching {
        TimerManager.cancelTask(context, serviceTaskTag)
        job?.cancel()
        job = null
    }.onFailure {
        Logger.error("Error stopping service $serviceClass", it)
    }
}
