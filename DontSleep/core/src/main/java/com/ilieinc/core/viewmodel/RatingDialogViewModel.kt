package com.ilieinc.core.viewmodel

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat
import com.ilieinc.core.util.Logger
import com.ilieinc.core.viewmodel.base.DialogViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class RatingDialogViewModel(
    onDismissRequestedCallback: () -> Unit,
    application: Application
) : DialogViewModel(onDismissRequestedCallback, application) {
    fun requestReview() {
        with(Uri.parse("market://details?id=${getApplication<Application>().packageName}")) {
            with(Intent(Intent.ACTION_VIEW, this)) {
                try {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    ContextCompat.startActivity(getApplication(), this, null)
                } catch (ex: Exception) {
                    Logger.error(ex)
                }
            }
        }
        onDismissRequested()
    }
}