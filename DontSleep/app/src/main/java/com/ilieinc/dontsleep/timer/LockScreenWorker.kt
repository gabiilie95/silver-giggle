package com.ilieinc.dontsleep.timer

import android.app.admin.DevicePolicyManager
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.ilieinc.core.util.DeviceAdminHelper
import com.ilieinc.core.util.Logger

class LockScreenWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {
    override fun doWork(): Result {
        var result = Result.success()
        try {
            val deviceManager =
                ContextCompat.getSystemService(applicationContext, DevicePolicyManager::class.java)!!
            if(deviceManager.isAdminActive(DeviceAdminHelper.componentName)){
                deviceManager.lockNow()
            }
        } catch (ex: Exception) {
            Logger.error(ex)
            result = Result.failure()
        }
        return result
    }
}