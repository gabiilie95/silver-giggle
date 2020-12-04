package com.ilieinc.dontsleep.ui.main

import android.app.AlertDialog
import android.app.admin.DevicePolicyManager
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.TimePicker
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.switchmaterial.SwitchMaterial
import com.ilieinc.dontsleep.R
import com.ilieinc.dontsleep.model.DeviceAdminChangedEvent
import com.ilieinc.dontsleep.model.ServiceStatusChangedEvent
import com.ilieinc.dontsleep.receiver.DeviceAdminReceiver
import com.ilieinc.dontsleep.service.SleepService
import com.ilieinc.dontsleep.service.TimeoutService
import com.ilieinc.dontsleep.util.*
import com.ilieinc.dontsleep.util.StateHelper.createDialog
import com.ilieinc.dontsleep.util.StateHelper.startForegroundService
import com.ilieinc.dontsleep.util.StateHelper.stopService
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainFragment : Fragment(), ServiceStatusChangedEvent, DeviceAdminChangedEvent {

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
        deviceManager = getSystemService(requireContext(), DevicePolicyManager::class.java)!!
    }

    override fun onStart() {
        TimeoutService.serviceStatusChanged addSubscriber this
        SleepService.serviceStatusChanged addSubscriber this
        DeviceAdminReceiver.statusChangedEvent addSubscriber this
        super.onStart()
        initControls()
    }

    override fun onStop() {
        TimeoutService.serviceStatusChanged removeSubscriber this
        SleepService.serviceStatusChanged removeSubscriber this
        DeviceAdminReceiver.statusChangedEvent removeSubscriber this
        super.onStop()
    }

    private fun initControls() {
        setTimeoutControls(PermissionHelper.shouldRequestDrawOverPermission(requireContext()))
        setSleepTimerControls(PermissionHelper.shouldRequestAdminPermission(deviceManager))
        initButtons()
    }

    private fun initButtons() {
        draw_over_permission_help_button.setOnClickListener {
            val dialog = StateHelper.getTimeoutInfoDialog(requireActivity()) {
                PermissionHelper.requestDrawOverPermission(requireContext())
            }
            dialog.show()
        }
        helpButton.setOnClickListener {
            val dialog = DeviceAdminHelper.getInfoDialog(requireActivity()) {
                deviceManager.removeActiveAdmin(DeviceAdminHelper.componentName)
            }
            dialog.show()
        }
        closeButton.setOnClickListener {
            activity?.finish()
        }
    }

    override fun onServiceStatusChanged(serviceName: String, active: Boolean) {
        activity?.runOnUiThread {
            when (serviceName) {
                TimeoutService::class.java.name -> {
                    timeoutSwitch.isChecked = active
                }
                SleepService::class.java.name -> {
                    sleepSwitch.isChecked = active
                }
            }
        }
    }

    override fun onDeviceAdminStatusChanged(active: Boolean) {
        setSleepTimerControls(!active)
    }

    private fun setStatus(
        statusSwitch: SwitchMaterial,
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

    private fun setTimeoutControls(requiresPermission: Boolean) {
        setTimeoutControlsVisibility(!requiresPermission)
        if (requiresPermission) {
            draw_over_permission_button.setOnClickListener {
                createDialog(
                    requireContext(),
                    getString(R.string.draw_over_permission_grant_title),
                    getString(R.string.draw_over_permission_grant_body),
                    getString(R.string.yes),
                    getString(R.string.no)
                ) {
                    PermissionHelper.requestDrawOverPermission(requireContext())
                }.show()
            }
        } else {
            setStatus(
                timeoutSwitch,
                TimeoutService.isRunning(requireContext())
            ) { _, checked ->
                if (checked) {
                    requireContext().startForegroundService<TimeoutService>()
                } else {
                    requireContext().stopService<TimeoutService>()
                }
            }
            setTimePicker(timeoutTimePicker, TimeoutService.TIMEOUT_TAG)
        }
    }

    private fun setSleepTimerControls(requiresPermission: Boolean) {
        setSleepTimerControlsVisibility(!requiresPermission)
        if (requiresPermission) {
            permissionButton.setOnClickListener {
                createDialog(
                    requireContext(),
                    getString(R.string.special_permission_grant_title),
                    getString(R.string.special_permission_grant_body),
                    getString(R.string.yes),
                    getString(R.string.no)
                ) {
                    val dialog = createDialog(
                        requireContext(),
                        getString(R.string.warning),
                        getString(R.string.special_permission_grant_warning_body),
                        getString(R.string.yes),
                        getString(R.string.no)
                    ) {
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
                    }.create()
                    dialog.setOnShowListener {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                            ?.let { button ->
                                GlobalScope.launch {
                                    kotlin.runCatching {
                                        var seconds = 10
                                        val initialText = button.text
                                        this@MainFragment.requireActivity().runOnUiThread {
                                            button.isEnabled = false
                                        }
                                        do {
                                            this@MainFragment.requireActivity().runOnUiThread {
                                                button.text = "$initialText ($seconds)"
                                            }
                                            delay(1000)
                                        } while (seconds-- > 0)
                                        this@MainFragment.requireActivity().runOnUiThread {
                                            button.text = initialText
                                            button.isEnabled = true
                                        }
                                    }.onFailure { ex -> Logger.error(ex) }
                                }
                            }
                    }
                    dialog.show()
                }.show()
            }
        } else {
            setStatus(
                sleepSwitch,
                SleepService.isRunning(requireContext())
            ) { _, checked ->
                if (checked) {
                    requireContext().startForegroundService<SleepService>()
                } else {
                    requireContext().stopService<SleepService>()
                }
            }
            setTimePicker(sleepTimerTimePicker, SleepService.SLEEP_TAG)
        }
    }

    private fun setTimeoutControlsVisibility(enabled: Boolean) {
        val statusFieldsVisibility = if (enabled) View.VISIBLE else View.GONE
        val permissionButtonVisibility = if (enabled) View.GONE else View.VISIBLE
        timeout_body_layout.visibility = statusFieldsVisibility
        draw_over_permission_button.visibility = permissionButtonVisibility
    }

    private fun setSleepTimerControlsVisibility(enabled: Boolean) {
        val statusFieldsVisibility = if (enabled) View.VISIBLE else View.GONE
        val permissionButtonVisibility = if (enabled) View.GONE else View.VISIBLE
        sleepTimerLayout.visibility = statusFieldsVisibility
        permissionButton.visibility = permissionButtonVisibility
    }
}
