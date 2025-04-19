package com.ilieinc.core.viewmodel

import android.app.Application
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.ilieinc.core.util.Logger
import com.ilieinc.core.viewmodel.base.DialogViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RatingDialogViewModel @Inject constructor(
    application: Application
) : DialogViewModel(application) {
    fun requestReview() {
        with("market://details?id=${getApplication<Application>().packageName}".toUri()) {
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
