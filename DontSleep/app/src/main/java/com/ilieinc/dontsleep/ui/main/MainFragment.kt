package com.ilieinc.dontsleep.ui.main

import android.app.admin.DevicePolicyManager
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Switch
import android.widget.TimePicker
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ilieinc.dontsleep.R
import com.ilieinc.dontsleep.model.DeviceAdminChangedEvent
import com.ilieinc.dontsleep.model.WakeLockChangedEvent
import com.ilieinc.dontsleep.receiver.DeviceAdminReceiver
import com.ilieinc.dontsleep.service.WakeLockManager
import com.ilieinc.dontsleep.util.DeviceAdminHelper
import com.ilieinc.dontsleep.util.DeviceAdminHelper.createPermissionDialog
import com.ilieinc.dontsleep.util.SharedPreferenceManager
import kotlinx.android.synthetic.main.main_fragment.*

class MainFragment : Fragment(), WakeLockChangedEvent, DeviceAdminChangedEvent {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var deviceManager: DevicePolicyManager
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        deviceManager = getSystemService(context!!, DevicePolicyManager::class.java)!!
    }

    override fun onStart() {
        WakeLockManager.wakeLockStatusChanged addSubscriber this
        DeviceAdminReceiver.statusChangedEvent addSubscriber this
        super.onStart()
        initControls()
    }

    override fun onStop() {
        WakeLockManager.wakeLockStatusChanged removeSubscriber this
        DeviceAdminReceiver.statusChangedEvent removeSubscriber this
        super.onStop()
    }

    private fun initControls() {
        setAwakeStatus()
        setTimePicker(timeoutTimePicker, WakeLockManager.AWAKE_WAKELOCK_TAG)
        setSleepTimerControls(deviceManager.isAdminActive(DeviceAdminHelper.componentName))
        initButtons()
    }

    private fun initButtons() {
        helpButton.setOnClickListener {
            val dialog = DeviceAdminHelper.getInfoDialog(activity!!) {
                deviceManager.removeActiveAdmin(DeviceAdminHelper.componentName)
            }
            dialog.show()
        }
        closeButton.setOnClickListener {
            activity?.finish()
        }
    }

    override fun onWakeLockChanged(wakeLock: String, active: Boolean) {
        when (wakeLock) {
            WakeLockManager.AWAKE_WAKELOCK_TAG -> {
                statusSwitch.isChecked = active
            }
            WakeLockManager.SLEEP_TIMER_WAKELOCK_TAG -> {
                sleepTimerSwitch.isChecked = active
            }
        }
    }

    private fun setAwakeStatus() {
        setStatus(
            statusSwitch,
            WakeLockManager.isWakeLockActive(WakeLockManager.AWAKE_WAKELOCK_TAG)
        ) { compoundButton, checked ->
            WakeLockManager.setWakeLockStatus(
                compoundButton.context,
                WakeLockManager.AWAKE_WAKELOCK_TAG,
                checked,
                false
            )
        }
    }

    override fun onDeviceAdminStatusChanged(active: Boolean) {
        setSleepTimerControls(active)
    }

    private fun setStatus(
        statusSwitch: Switch,
        checked: Boolean,
        callback: (CompoundButton, Boolean) -> Unit
    ) {
        statusSwitch.isChecked = checked
        statusSwitch.setOnCheckedChangeListener(callback)
    }

    private fun setTimePicker(timePicker: TimePicker, value: String) {
        timePicker.setIs24HourView(true)
        val timeout =
            SharedPreferenceManager.getInstance(context!!).getLong(value, 900000) / 1000 / 60
        timePicker.hour = (timeout / 60).toInt()
        timePicker.minute = (timeout % 60).toInt()
        timePicker.setOnTimeChangedListener { view, hourOfDay, minute ->
            viewModel.saveTimePickerSettings(view.context, value, Pair(hourOfDay, minute))
        }
    }

    private fun setSleepTimerControls(adminActive: Boolean) {
        setSleepTimerControlsVisibility(adminActive)
        if (adminActive) {
            setStatus(
                sleepTimerSwitch,
                WakeLockManager.isWakeLockActive(WakeLockManager.SLEEP_TIMER_WAKELOCK_TAG)
            ) { compoundButton, checked ->
                WakeLockManager.setWakeLockStatus(
                    compoundButton.context,
                    WakeLockManager.SLEEP_TIMER_WAKELOCK_TAG,
                    checked,
                    true
                )
            }
            setTimePicker(sleepTimerTimePicker, WakeLockManager.SLEEP_TIMER_WAKELOCK_TAG)
        } else {
            permissionButton.setOnClickListener {
                val dialog = createPermissionDialog(activity!!) {
                    val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
                    intent.putExtra(
                        DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                        DeviceAdminHelper.componentName
                    )
                    intent.putExtra(
                        DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                        "Please allow this permission to allow the application to turn off the screen."
                    )
                    startActivityForResult(intent, DeviceAdminHelper.REQUEST_CODE)
                }
                dialog.show()
            }
        }
    }

    private fun setSleepTimerControlsVisibility(enabled: Boolean) {
        val statusFieldsVisibility = if (enabled) View.VISIBLE else View.GONE
        val permissionButtonVisibility = if (enabled) View.GONE else View.VISIBLE

        sleepTimerStatusText.visibility = statusFieldsVisibility
        sleepTimerSwitch.visibility = statusFieldsVisibility
        sleepTimerLayout.visibility = statusFieldsVisibility
        permissionButton.visibility = permissionButtonVisibility
    }
}
