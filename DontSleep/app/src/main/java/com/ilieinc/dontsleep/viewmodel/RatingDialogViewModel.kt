package com.ilieinc.dontsleep.viewmodel

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat
import com.ilieinc.core.util.Logger
import com.ilieinc.dontsleep.viewmodel.base.DialogViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class RatingDialogViewModel(
    showDialog: MutableStateFlow<Boolean>,
    application: Application
) : DialogViewModel(showDialog, application) {
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
        showDialog.tryEmit(false)
    }
}