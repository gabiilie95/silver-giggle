package com.ilieinc.core.util

import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.core.content.ContextCompat
import com.ilieinc.core.data.CoreDataStore
import com.ilieinc.core.data.CoreDataStore.APP_START_COUNT_PREF_KEY
import com.ilieinc.core.data.CoreDataStore.RATING_SHOWN_PREF_KEY
import com.ilieinc.core.data.dataStore
import com.ilieinc.core.data.getValue
import com.ilieinc.core.data.setValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Locale
import androidx.core.net.toUri

object StateHelper {
    enum class TileStates {
        On,
        Off,
        Disabled
    }

    private val overlayDevices = arrayOf(
        "samsung"
    )

    private val _useDynamicColors = MutableStateFlow(true)
    val useDynamicColors = _useDynamicColors.asStateFlow()

    fun deviceRequiresOverlay() = overlayDevices.contains(Build.MANUFACTURER.lowercase(Locale.getDefault()))

    /**
     * Returns false instead of throwing when the platform refuses the start. Android 12+
     * rejects foreground service starts made without a background-start exemption, and
     * that rejection reaches the caller as well as the service itself.
     */
    inline fun Context.startForegroundService(
        serviceClass: Class<*>,
        extraActions: (intent: Intent) -> Unit = {}
    ): Boolean = runCatching {
        ContextCompat.startForegroundService(this, Intent(this, serviceClass).apply {
            extraActions(this)
        })
        true
    }.getOrElse {
        Logger.error("Could not start ${serviceClass.simpleName}", it)
        false
    }

    inline fun <reified T> Context.startForegroundService(extraActions: (intent: Intent) -> Unit = {}): Boolean where T : Service =
        this.startForegroundService(T::class.java, extraActions)

    fun Context.stopService(serviceClass: Class<*>) {
        stopService(Intent(this, serviceClass))
    }

    inline fun <reified T> Context.stopService() where T : Service {
        stopService(Intent(this, T::class.java))
    }

    suspend fun updateRatingCountIfNeeded(context: Context) {
        with(context.dataStore) {
            val ratingWasShown = getValue(RATING_SHOWN_PREF_KEY, false)
            if (!ratingWasShown) {
                val startNum = getValue(APP_START_COUNT_PREF_KEY, 0)
                setValue(APP_START_COUNT_PREF_KEY, startNum + 1)
            }
        }
    }

    suspend fun initDynamicColorsEnabledProperty(context: Context) {
        with(context.dataStore) {
            _useDynamicColors.update {
                getValue(
                    CoreDataStore.SHOULD_USE_DYNAMIC_COLORS_PREF_KEY,
                    true
                )
            }
        }
    }

    suspend fun setDynamicColorsEnabled(context: Context, enabled: Boolean) {
        with(context.dataStore) {
            setValue(CoreDataStore.SHOULD_USE_DYNAMIC_COLORS_PREF_KEY, enabled)
            _useDynamicColors.update { enabled }
        }
    }

    suspend fun needToShowReviewSnackbar(context: Context): Boolean {
        with(context.dataStore) {
            val snackbarWasShown = getValue(RATING_SHOWN_PREF_KEY, false)
            val startNum = getValue(APP_START_COUNT_PREF_KEY, 0)
            return !snackbarWasShown && startNum != 0 && startNum % 5 == 0
        }
    }

    suspend fun showReviewSnackbar(context: Context, snackbarHostState: SnackbarHostState) {
        val result = snackbarHostState.showSnackbar(
            "Please rate the app if you are enjoying it :)",
            actionLabel = "RATE APP",
            duration = SnackbarDuration.Short
        )
        if (result == SnackbarResult.ActionPerformed) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    "market://details?id=${context.packageName}".toUri()
                ).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            )
            context.dataStore.setValue(RATING_SHOWN_PREF_KEY, true)
        }
    }
}